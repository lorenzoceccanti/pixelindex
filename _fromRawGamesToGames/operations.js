// CONVERSIONE DA RAW_GAMES IN GAMES:

// STATUS:
db.games.updateMany({ status: { $exists: true }}, [
  {
    $set: {
      status: {
        $concat: [
          "$status.status_value"
        ]
      }
    }
  }
])


// AGE_RATINGS:
db.games.updateMany(
  { age_ratings: { $exists: true }},
  [
    {
      $set: {
        age_ratings: {
          $map: {
            input: "$age_ratings",
            as: "rating",
            in: {
              ratingSystem: "$$rating.age_rating_category.age_rating_category_value",
              ratingValue: "$$rating.age_rating_rating.age_rating_rating_value"
            }
          }
        }
      }
    }
  ]
)

//COMPANIES:
db.games.updateMany(
  { companies: { $exists: true } },
  [
    {
      $set: {
        companies: {
          $map: {
            input: "$companies",
            as: "var",
            in: "$$var.company_name"
          }
        }
      }
    }
  ]
)

// PLATFORMS:
db.games.updateMany(
  { platforms: { $exists: true } },
  [
    {
      $set: {
        platforms: {
          $map: {
            input: "$platforms",
            as: "var",
            in: "$$var.platforms_value"
          }
        }
      }
    }
  ]
)

// GENRES:
db.games.updateMany(
  { genres: { $exists: true } },
  [
    {
      $set: {
        genres: {
          $map: {
            input: "$genres",
            as: "var",
            in: "$$var.genres_value"
          }
        }
      }
    }
  ]
)

// GAME_MODES:
db.games.updateMany(
  { game_modes: { $exists: true } },
  [
    {
      $set: {
        game_modes: {
          $map: {
            input: "$game_modes",
            as: "var",
            in: "$$var.game_modes_value"
          }
        }
      }
    }
  ]
)


// CATEGORY:
db.games.updateMany({ category: { $exists: true }}, [
  {
    $set: {
      category: {
        $concat: [
          "$category.category_value"
        ]
      }
    }
  }
])


// FIRST_RELEASE_DATE:
db.games.updateMany(
    { first_release_date: { $exists: true }},
    [
        {
            $set: {
                first_release_date: {
                    $toDate: {
                        $multiply: [
                            { $toDouble: "$first_release_date" },
                            1000
                        ]
                    }
                }
            }
        }
    ]
);
