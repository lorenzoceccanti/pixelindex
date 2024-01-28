package it.unipi.largescale.pixelindex.dao.impl;

import com.mongodb.client.FindIterable;
import it.unipi.largescale.pixelindex.dao.GameMongoDAO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Company;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.model.Genre;
import static it.unipi.largescale.pixelindex.utils.Utils.convertDateToLocalDate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class GameMongoDAOImpl extends BaseMongoDAO implements GameMongoDAO {

    private Game gameFromQueryResult(Document result) {
        Game game = new Game();
        ObjectId resultObjectId = result.getObjectId("_id");
        game.setId(resultObjectId.toString());
        if (result.containsKey("name")) {
            game.setName(result.getString("name"));
        }
        if (result.containsKey("category")) {
            game.setCategory(result.getString("category"));
        }
        if (result.containsKey("first_release_date")) {
            game.setReleaseDate(convertDateToLocalDate(result.getDate("first_release_date")));
        }
        if (result.containsKey("game_modes")) {
            List<String> gameModes = result.getList("game_modes", String.class);
            game.setGameModes(gameModes.toArray(new String[0]));
        }
        if (result.containsKey("genres")) {
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
        if (result.containsKey("companies")) {
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
        if (result.containsKey("languages")) {
            List<String> languages = result.getList("languages", String.class);
            game.setLanguages(languages.toArray(new String[0]));
        }
        if (result.containsKey("summary")) {
            game.setSummary(result.getString("summary"));
        }
        if (result.containsKey("platforms")) {
            List<String> platforms = result.getList("platforms", String.class);
            game.setPlatforms(platforms.toArray(new String[0]));
        }
        return game;
    }

    @Override
    public List<Game> getGamesByName(String name) throws DAOException {
        List<Game> games = new ArrayList<>();
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");
            // We use a regex to perform a case-insensitive search and find all the games whose name contains the
            // specified string
            Document query = new Document("name", new Document("$regex", name).append("$options", "i"));
            FindIterable<Document> results = collection.find(query);
            for (Document result : results) {
                games.add(gameFromQueryResult(result));
            }
        } catch (Exception e) {
            throw new DAOException("Error retrieving game by name " + e);
        }
        return games;
    }
    @Override
    public Game getGameById(String id) throws DAOException {
        Game gameObject = null;
        try (MongoClient mongoClient = beginConnection()) {
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

    @Override
    public void insertGame(Game game) throws DAOException {
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
        } catch (Exception e) {
            throw new DAOException("Error inserting game " + e);
        }
    }
}
