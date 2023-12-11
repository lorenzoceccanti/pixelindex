import json

def transform_json_structure(input_file_path, output_file_path):
    try:
        # Aprire il file JSON originale
        with open(input_file_path, 'r', encoding='utf-8') as file:
            data = json.load(file)

        # Trasformare il dizionario in una lista di oggetti
        transformed_data = [value for key, value in data.items()]

        # Salvare il nuovo JSON
        with open(output_file_path, 'w', encoding='utf-8') as file:
            json.dump(transformed_data, file, indent=4)

        return "Trasformazione completata con successo."

    except Exception as e:
        return f"Si è verificato un errore: {e}"

# Percorsi dei file (da modificare in base al percorso effettivo del file)
input_file_path = './games_dataset_reviews.json'  # Percorso del file JSON originale
output_file_path = './games_dataset_reviews_transformed.json'  # Percorso del file JSON trasformato

# Chiamare la funzione per trasformare la struttura del JSON
result = transform_json_structure(input_file_path, output_file_path)
result

