package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.client.*;
import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;

import static it.unipi.largescale.pixelindex.utils.Utils.convertDateToLocalDate;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Field;
import org.bson.conversions.Bson;

import java.time.ZoneId;
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
            List<String> genres = result.getList("genres", String.class);
            game.setGenres(genres.toArray(new String[0]));
        }
        if (result.containsKey("companies") && result.getList("companies", String.class) != null) {
            List<String> companies = result.getList("companies", String.class);
            game.setCompanies(companies.toArray(new String[0]));
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
        if (result.containsKey("pegiRating") && result.getString("pegiRating") != null) {
            game.setPegiRating(result.getString("pegiRating"));
        }
        return game;
    }

    private GamePreviewDTO gamePreviewFromQueryResult(Document result) {
        GamePreviewDTO game = new GamePreviewDTO();
        game.setId(result.getObjectId("_id").toString());
        game.setName(result.getString("name"));

        if (result.containsKey("first_release_date") && result.getDate("first_release_date") != null) {
            game.setReleaseYear(convertDateToLocalDate(result.getDate("first_release_date")).getYear());
        }

        game.setPegiRating(result.getString("pegiRating"));
        return game;
    }

    public List<GamePreviewDTO> getGamesAdvancedSearch(String name, String company, String platform, Integer releaseYear, int page) throws DAOException {
        List<GamePreviewDTO> games = new ArrayList<>();
        try (MongoClient mongoClient = beginConnection(false)) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");

            List<Bson> aggregationPipeline = new ArrayList<>();

            // Filter only documents with consistent equal to true
            Document matchCriteria = new Document("consistent", true);
            // Text search for name
            if (name != null && !name.isEmpty()) {
                matchCriteria.append("$text", new Document("$search", name));
            }
            if (releaseYear != null) {
                Date startDate = new GregorianCalendar(releaseYear, Calendar.JANUARY, 1).getTime();
                Date endDate = new GregorianCalendar(releaseYear + 1, Calendar.JANUARY, 1).getTime();
                matchCriteria.append("first_release_date", new Document("$gte", startDate).append("$lt", endDate));
            }
            if (company != null && !company.isEmpty()) {
                matchCriteria.append("companies", new Document("$regex", company).append("$options", "i"));
            }
            if (platform != null && !platform.isEmpty()) {
                matchCriteria.append("platforms", new Document("$regex", platform).append("$options", "i"));
            }

            aggregationPipeline.add(Aggregates.match(matchCriteria));

            // Add textScore for sorting
            aggregationPipeline.add(Aggregates.addFields(new Field<>("textScore", new Document("$meta", "textScore"))));

            // Prioritizing main_games and textScore
            aggregationPipeline.add(
                    Aggregates.addFields(new Field<>("isMainGame",
                            new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$category", "main_game")), 1, 0))))
            );

            // Sorting stage modified to textScore and main games
            aggregationPipeline.add(Aggregates.sort(Sorts.orderBy(Sorts.descending("textScore", "isMainGame"))));

            // Pagination
            aggregationPipeline.add(Aggregates.skip(10 * page));
            aggregationPipeline.add(Aggregates.limit(10));

            // Projection
            aggregationPipeline.add(Aggregates.project(new Document("_id", 1L)
                    .append("name", 1L)
                    .append("first_release_date", 1L)
                    .append("pegiRating", 1L)));

            // Aggregation
            ArrayList<Document> results = collection.aggregate(aggregationPipeline).into(new ArrayList<>());

            for (Document result : results) {
                GamePreviewDTO game = gamePreviewFromQueryResult(result);
                games.add(game);
            }
        } catch (Exception e) {
            throw new DAOException("Error retrieving games: " + e.getMessage());
        }
        return games;
    }

    public Game getGameById(String id) throws DAOException {
        Game gameObject = null;
        try (MongoClient mongoClient = beginConnection(false)) {
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
        try (MongoClient mongoClient = beginConnection(false)) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");

            Document document = new Document();
            document.append("name", game.getName());
            document.append("category", game.getCategory());
            Date date = Date.from(game.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            document.append("first_release_date", date);

            // I have to convert the arrays in list of strings
            List<String> gameModesList = Arrays.asList(game.getGameModes());
            List<String> genresList = Arrays.asList(game.getGenres());

            List<String> companiesList = Arrays.asList(game.getCompanies());
            List<String> languagesList = Arrays.asList(game.getLanguages());
            List<String> platformsList = Arrays.asList(game.getPlatforms());

            document.append("game_modes", gameModesList);
            document.append("genres", genresList);
            document.append("companies", companiesList);
            document.append("languages", languagesList);
            document.append("platforms", platformsList);
            document.append("summary", game.getSummary());
            document.append("consistent", false);
            collection.insertOne(document);
            return document.get("_id").toString();
        } catch (Exception e) {
            throw new DAOException("Error inserting game " + e);
        }
    }

    public void updateConsistencyFlag(String gameId) throws DAOException {
        try (MongoClient mongoClient = beginConnection(false)) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");
            Document filter = new Document("_id", new ObjectId(gameId));
            Document updateOperation = new Document("$set", new Document("consistent", true));
            collection.updateOne(filter, updateOperation);
        } catch (Exception e) {
            throw new DAOException("Error updating consistency flag: " + e);
        }
    }

    public ArrayList<Game> getInconsistentGames() throws DAOException {
        try (MongoClient mongoClient = beginConnection(false)) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");
            Document filter = new Document("consistent", false);
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
