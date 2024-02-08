package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.client.*;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Company;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.model.Genre;

import static it.unipi.largescale.pixelindex.utils.Utils.convertDateToLocalDate;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Field;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class GameMongoDAO extends BaseMongoDAO {

    private Game gameFromQueryResult(Document result) {
        Game game = new Game();
        ObjectId resultObjectId = result.getObjectId("_id");
        game.setId(resultObjectId.toString());
        if (result.containsKey("name") && result.getString("name") != null) {
            game.setName(result.getString("name"));
        }
        if (result.containsKey("category") && result.getString("category") != null) {
            game.setCategory(result.getString("category"));
        }
        if (result.containsKey("first_release_date") && result.getDate("first_release_date") != null) {
            game.setReleaseDate(convertDateToLocalDate(result.getDate("first_release_date")));
        }
        if (result.containsKey("game_modes") && result.getList("game_modes", String.class) != null) {
            List<String> gameModes = result.getList("game_modes", String.class);
            game.setGameModes(gameModes.toArray(new String[0]));
        }
        if (result.containsKey("genres") && result.getList("genres", String.class) != null) {
            List<String> genresNames = result.getList("genres", String.class);
            List<Genre> genres = genresNames.stream()
                    .map(name -> {
                        Genre genre = new Genre();
                        genre.setName(name);
                        return genre;
                    })
                    .toList();
            game.setGenres(genres.toArray(new Genre[0]));
        }
        if (result.containsKey("companies") && result.getList("companies", String.class) != null) {
            List<String> companiesNames = result.getList("companies", String.class);
            List<Company> companies = (companiesNames.stream()
                    .map(name -> {
                        Company company = new Company();
                        company.setName(name);
                        return company;
                    })
                    .toList());
            game.setCompanies(companies.toArray(new Company[0]));
        }
        if (result.containsKey("language_supports") && result.getList("language_supports", String.class) != null) {
            List<String> languages = result.getList("language_supports", String.class);
            game.setLanguages(languages.toArray(new String[0]));
        }
        if (result.containsKey("summary") && result.getString("summary") != null) {
            game.setSummary(result.getString("summary"));
        }
        if (result.containsKey("platforms") && result.getList("platforms", String.class) != null) {
            List<String> platforms = result.getList("platforms", String.class);
            game.setPlatforms(platforms.toArray(new String[0]));
        }
        if (result.containsKey("status") && result.getString("status") != null) {
            game.setStatus(result.getString("status"));
        }
        return game;
    }

    public List<Game> getGamesAdvancedSearch(String name, String company, String platform, Integer releaseYear, int page) throws DAOException {
        List<Game> games = new ArrayList<>();
        try (MongoClient mongoClient = beginConnectionWithoutReplica()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");

            List<Bson> aggregationPipeline = new ArrayList<>();

            // Search criteria
            if (name != null && !name.isEmpty()) {
                aggregationPipeline.add(Aggregates.match(Filters.regex("name", name, "i")));
            }
            if (releaseYear != null) {
                Date startDate = new GregorianCalendar(releaseYear, Calendar.JANUARY, 1).getTime();
                Date endDate = new GregorianCalendar(releaseYear + 1, Calendar.JANUARY, 1).getTime();
                aggregationPipeline.add(Aggregates.match(Filters.and(Filters.gte("first_release_date", startDate),
                        Filters.lt("first_release_date", endDate))));
            }
            if (company != null && !company.isEmpty()) {
                aggregationPipeline.add(Aggregates.match(Filters.regex("companies", company, "i")));
            }
            if (platform != null && !platform.isEmpty()) {
                aggregationPipeline.add(Aggregates.match(Filters.regex("platforms", platform, "i")));
            }

            // Prioritizing main_games
            aggregationPipeline.add(
                    Aggregates.addFields(new Field<>("isMainGame",
                            new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$category", "main_game")), 1, 0)))));

            // Sorting stage
            aggregationPipeline.add(Aggregates.sort(Sorts.orderBy(Sorts.descending("isMainGame"), Sorts.ascending("name"))));

            // Pagination
            aggregationPipeline.add(Aggregates.skip(10 * page));
            aggregationPipeline.add(Aggregates.limit(10));

            // Aggregation
            ArrayList<Document> results = collection.aggregate(aggregationPipeline).into(new ArrayList<>());
            for (Document result : results) {
                games.add(gameFromQueryResult(result));
            }
        } catch (Exception e) {
            throw new DAOException("Error retrieving games: " + e.getMessage());
        }
        return games;
    }

    public Game getGameById(String id) throws DAOException {
        Game gameObject = null;
        try (MongoClient mongoClient = beginConnectionWithoutReplica()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");
            ObjectId objectId = new ObjectId(id);
            Document query = new Document("_id", objectId);
            Document result = collection.find(query).first();
            if (result != null)
                gameObject = gameFromQueryResult(result);

        } catch (Exception e) {
            throw new DAOException("Error retrieving game by ID " + e);
        }
        return gameObject;
    }

    public String insertGame(Game game) throws DAOException {
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");
            Document document = new Document();
            document.append("name", game.getName());
            document.append("category", game.getCategory());
            document.append("first_release_date", game.getReleaseDate());
            document.append("game_modes", game.getGameModes());
            document.append("genres", game.getGenres());
            document.append("companies", game.getCompanies());
            document.append("languages", game.getLanguages());
            document.append("summary", game.getSummary());
            document.append("platforms", game.getPlatforms());
            collection.insertOne(document);
            return document.get("_id").toString();
        } catch (Exception e) {
            throw new DAOException("Error inserting game " + e);
        }
    }

    public void updateConsistencyFlag(String gameId) throws DAOException {
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");
            Document filter = new Document("_id", new ObjectId(gameId));
            Document updateOperation = new Document("$set", new Document("consistency", true));
            collection.updateOne(filter, updateOperation);
        } catch (Exception e) {
            throw new DAOException("Error updating consistency flag: " + e);
        }
    }

    public ArrayList<Game> getInconsistentGames() throws DAOException {
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");
            Document filter = new Document("consistent", true);
            ArrayList<Document> results = collection.find(filter).into(new ArrayList<>());
            ArrayList<Game> games = new ArrayList<>();
            for (Document result : results) {
                games.add(gameFromQueryResult(result));
            }
            return games;
        } catch (Exception e) {
            throw new DAOException("Error updating consistency flag: " + e);
        }
    }
}
