/* CRUD OPERATION: Inserimento di una nuova segnalazione con il criterio concordato */
/* Sostituire ale1968 con l'utente incriminato e Chang Liu con l'utente che segnala*/
db.users.updateOne(
  {
    username: "ale1968",
    $or: [
      {$expr: {$lt: [{ $size: "$reported_by" }, 5] } },
      { "reported_by": { $exists: false } }
    ]
  },
  {
    $addToSet: { reported_by: "Chang Liu" }
  }
)

/* QUERY DI AGGIORNAMENTO PER BANNARE L'UTENTE */
db.users.updateOne(
  {
    username: "ale1968"
  },
  {
    $set: {isBanned: true}
  }
)


