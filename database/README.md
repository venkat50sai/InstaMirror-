# InstaMirror Database Scripts

This folder contains MySQL CREATE TABLE scripts for each backend microservice.

Files:
- `00-create-database.sql` — creates the `instamirror_db` database and selects it.
- `user-service.sql` — schema for the user microservice.
- `product-service.sql` — schema for the product microservice.
- `post-service.sql` — schema for the post microservice.
- `comment-service.sql` — schema for the comment microservice.
- `like-service.sql` — schema for the like microservice.
- `friend-service.sql` — schema for the friend microservice.
- `order-service.sql` — schema for the order microservice.
- `cart-service.sql` — schema for the cart microservice.

## How to use

Run these scripts in MySQL after creating the database: 

```sql
SOURCE 00-create-database.sql;
SOURCE user-service.sql;
SOURCE product-service.sql;
SOURCE post-service.sql;
SOURCE comment-service.sql;
SOURCE like-service.sql;
SOURCE friend-service.sql;
SOURCE order-service.sql;
SOURCE cart-service.sql;
```

If you are using an environment with a single database, run the scripts in the order shown above.
