import requests
import threading
import json
import os
from dotenv import load_dotenv

# Constants
load_dotenv()

CLIENT_ID = os.getenv("CLIENT_ID")
IGDB_BASE_URI = "https://api.igdb.com"
AUTH_HEADER = f'Bearer {os.getenv("IGDB_BEARER")}'
GAMES_ENDPOINT = "/v4/games"
EXTERNAL_GAMES_ENDPOINT = "/v4/external_games"
PAYLOAD = {'headers': {'Client-ID': CLIENT_ID, 'Authorization': AUTH_HEADER}}

# Global Variables
data = {}
offset_lock = threading.Lock()
games_fetch_completed = False
steam_fetch_completed = False

# Exception for request failures
class RequestFailedException(Exception):
    pass

# IGDB API Fetch Function
def fetch_games(offset):
    PAYLOAD["data"] = f'fields *; limit 500; offset {offset};'
    response = requests.post(IGDB_BASE_URI + GAMES_ENDPOINT, headers=PAYLOAD['headers'], data=PAYLOAD['data'])
    if response.status_code != 200:
        raise RequestFailedException(f"Request failed with status code: {response.status_code}")
    for elem in response.json():
        data[elem["id"]] = elem
    return response.json()

# Function to Fetch Games in a Loop
def fetch_games_loop():
    global games_fetch_completed, offset
    while True:
        curr_offset = None
        with offset_lock:
            print(f"Fetching game with offset {offset}")
            curr_offset = offset
        try:
            fetched_games = len(fetch_games(curr_offset))
            if fetched_games < 500:
                with condition:
                    if not games_fetch_completed:
                        games_fetch_completed = True
                        print("Signaling fetch completed")
                        condition.notify()
                break
            else:
                with offset_lock:
                    offset += 500
        except RequestFailedException as e:
            print(e)

# Function to Populate Steam App IDs
def populate_steam_app_id(offset):
    PAYLOAD["data"] = f'fields *; limit 500; offset {offset};'
    response = requests.post(IGDB_BASE_URI + EXTERNAL_GAMES_ENDPOINT, headers=PAYLOAD['headers'], data=PAYLOAD['data'])
    if response.status_code != 200:
        print(response.text)
        raise RequestFailedException(f"Request failed with status code: {response.status_code}")
    for elem in response.json():
        if elem["category"] == 1 and elem["game"] in data and "url" in elem:
            data[elem["game"]]["steam"] = elem["url"].split("/")[-1]
    return response.json()
# Function to Populate Steam App IDs in a Loop
def populate_steam_app_ids_loop():
    global steam_fetch_completed, offset
    while True:
        curr_offset = None
        with offset_lock:
            print(f"Fetching steam app id with offset {offset}")
            curr_offset = offset
        try:
            fetched_games = len(populate_steam_app_id(curr_offset))
            if fetched_games < 500:
                with condition:
                    if not steam_fetch_completed:
                        steam_fetch_completed = True
                        print("Signaling fetch completed")
                        condition.notify()
                break
            else:
                with offset_lock:
                    offset += 500
        except RequestFailedException as e:
            print(e)

# Main Function
if __name__ == "__main__":
    condition = threading.Condition()
    offset = 0

    # Fetching games
    for _ in range(2):
        threading.Thread(target=fetch_games_loop).start()

    with condition:
        condition.wait_for(lambda: games_fetch_completed)

    print("Fetching games completed")
    # Resetting offset for Steam ID population
    offset = 0

    # Populating Steam IDs
    for _ in range(2):
        threading.Thread(target=populate_steam_app_ids_loop).start()

    with condition:
        condition.wait_for(lambda: steam_fetch_completed)

    print("Fetching steam app ID completed")
    # Writing data to file
    with open("games_dataset.json", "w") as f:
        json.dump(data, f, indent=4)

