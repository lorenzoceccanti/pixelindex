/* Used by the moderator to print the top 10 reported users
among the ones having at least one report*/
db.users.aggregate([
    {
        $match:{
            "reported_by": { $exists: true }
        }
    },
    {
      $project: {
        _id: 0,
        username: 1,
        numberReports: { $size: "$reported_by" }
      }
    },
    {
      $sort: { numberReports: -1 }
    },
    {
      $limit: 10
    }
  ])
  