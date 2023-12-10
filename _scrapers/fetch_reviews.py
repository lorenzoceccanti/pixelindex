import requests
import json
from bs4 import BeautifulSoup
import threading
from concurrent.futures import ThreadPoolExecutor
import time

# Function to process each game
def process_game(key, game):
    if "steam" in game:
        print(game["name"])
        url = f"https://store.steampowered.com/appreviews/{game['steam']}?cursor=*&day_range=30&start_date=-1&end_date=-1&date_range_type=all&filter=summary&language=english&l=english&review_type=all&purchase_type=all&playtime_filter_min=0&playtime_filter_max=0&filter_offtopic_activity=1&summary_num_positive_reviews=489&summary_num_reviews=557"
        r = requests.get(url)
        soup = BeautifulSoup(r.json()["html"], "html.parser")
        review_box = soup.find_all("div", {"class":"review_box"}) 
        reviews = []
        for review in review_box:
            try:
                recommended = None 
                try:
                    recommended = review.find("div",{"class":"title ellipsis"}).text == "Recommended"
                except:
                    pass
                review = {
                            "review": review.find("div", {"class": "content"}).text.strip(), 
                            "author": review.find("div", {"class": "persona_name"}).text.strip(), 
                            "recommended":recommended, 
                            "postedDate":review.find("div",{"class":"postedDate"}).text.replace("Posted: ","").replace("Direct from Steam", "").strip()
                         }
                reviews.append(review)
            except Exception as e:
                print(review)
                print(
                    type(e).__name__,          
                    __file__,                  
                    e.__traceback__.tb_lineno  
                )
        with threading.Lock():  
            games[key]["reviews"] = reviews

# Load games from JSON
games = None
with open("games_dataset.json", "r") as f:
    games = json.loads(f.read())

# Using ThreadPoolExecutor to parallelize the task
with ThreadPoolExecutor(max_workers=25) as executor:
    for key, game in games.items():
        executor.submit(process_game, key, game)

# Writing the updated games data to a new JSON file
with open("games_dataset_reviews.json", "w") as f:
    json.dump(games, f, indent=4)
