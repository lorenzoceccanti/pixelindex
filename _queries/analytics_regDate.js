/* 	- Si aggiunge un campo con data di registrazione dentro users:
	Per gli utenti già registrati si genera il campo (stando attenti a mettere il campo registrato prima della registrazione più vecchia)
	Per gli utenti che si registrano si mette la current_date 
	Scelto l'anno, si mostra per ciascun mese il numero di utenti per fasce d'età che si sono registrati:
	Mese | NumUtenti 14-18	| NumUtenti 19-24	| NumUtenti24+
    */

	[
		{
		  $match:
			/**
			 * query: The query in MQL.
			 */
			{
			  $expr: {
				$eq: [
				  {
					$year: "$registrationDate",
				  },
				  2023,
				],
			  },
			},
		},
		{
		  $project: {
			month: {
			  $month: "$registrationDate",
			},
			age: {
			  $subtract: [
				2024,
				{
				  $year: "$dateOfBirth",
				},
			  ],
			},
		  },
		},
		{
		  $group: {
			_id: {
			  month: "$month",
			  ageGroup: {
				$switch: {
				  branches: [
					{
					  case: {
						$lt: ["$age", 18],
					  },
					  then: "< 18 anni",
					},
					{
					  case: {
						$and: [
						  {
							$gte: ["$age", 18],
						  },
						  {
							$lte: ["$age", 30],
						  },
						],
					  },
					  then: "18-30 anni",
					},
					{
					  case: {
						$and: [
						  {
							$gt: ["$age", 30],
						  },
						  {
							$lte: ["$age", 50],
						  },
						],
					  },
					  then: "30-50 anni",
					},
					{
					  case: {
						$gt: ["$age", 50],
					  },
					  then: "> 50 anni",
					},
				  ],
				  default: "Altro",
				},
			  },
			},
			count: {
			  $sum: 1,
			},
		  },
		},
		{
		  $group: {
			_id: {
			  month: "$_id.month",
			},
			properties: {
			  $push: {
				ageGroup: "$_id.ageGroup",
				count: "$count",
			  },
			},
		  },
		},
		{
		  $project: {
			month: "$_id.month",
			properties: 1,
			_id: 0,
		  },
		},
		{
		  $sort:
			/**
			 * Provide any number of field/order pairs.
			 */
			{
			  month: 1,
			},
		},
	  ]
    
