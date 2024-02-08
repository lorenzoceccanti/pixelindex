/* Query che setta su mongo i seguiti di A e seguaci di B*/
db.prova.updateMany(
    { $or: [ { username: "maskedgingerjock" }, { username: "MisterEcho" } ] },
    [
      {
        $set: {
          followers: { $cond: { if: { $eq: [ "$username", "MisterEcho" ] }, then: 33, else: "$followers" } }, /* nuovi seguaci di B*/
          following: { $cond: { if: { $eq: [ "$username", "maskedgingerjock" ] }, then: 30, else: "$following" } } /* nuovi seguiti di A*/
        }
      }
    ]
  )
  