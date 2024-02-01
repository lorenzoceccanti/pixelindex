/* Dropping all the userWishlist and userLibrary fields (unused) from the collection of users*/
db.getCollection('users').update({}, {$unset: {userWishlist:1 ,userLibrary:1}}, {multi: true});
