package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.model.Company;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.model.Genre;
import static it.unipi.largescale.pixelindex.utils.Utils.convertDateToLocalDate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.List;

public class GameMongoDAO extends BaseMongoDAO implements GameDAO {

    @Override
    public Game getGameById(String id) {
        Game gameObject = null;
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("games");
            Document query = new Document("_id", id);
            Document result = collection.find(query).first();
            if (result != null) {
                gameObject = new Game();
                gameObject.setId(result.getString("_id"));
                if (result.containsKey("name")) {
                    gameObject.setName(result.getString("name"));
                }
                if (result.containsKey("category")) {
                    gameObject.setCategory(result.getString("category"));
                }
                if (result.containsKey("first_release_date")) {
                    gameObject.setReleaseDate(convertDateToLocalDate(result.getDate("first_release_date")));
                }
                if (result.containsKey("game_modes")) {
                    List<String> gameModes = result.getList("game_modes", String.class);
                    gameObject.setGameModes(gameModes.toArray(new String[0]));
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
                    gameObject.setGenres(genres.toArray(new Genre[0]));
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
                    gameObject.setCompanies(companies.toArray(new Company[0]));
                }
                if (result.containsKey("languages")) {
                    List<String> languages = result.getList("languages", String.class);
                    gameObject.setLanguages(languages.toArray(new String[0]));
                }
                if (result.containsKey("summary")) {
                    gameObject.setSummary(result.getString("summary"));
                }
                if (result.containsKey("platforms")) {
                    List<String> platforms = result.getList("platforms", String.class);
                    gameObject.setPlatforms(platforms.toArray(new String[0]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameObject;
    }
}
