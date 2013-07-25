-- Drop existing tables
drop table if exists product;
drop table if exists product_metadata;

-- Create Metadata table
create table product_metadata (
 id INTEGER PRIMARY KEY,
 urn VARCHAR(256) NOT NULL,
 UNIQUE(urn)
);

-- Create Product table
create table product (
 id INTEGER PRIMARY KEY,
 md_id INTEGER NOT NULL,
 urn VARCHAR(256) NOT NULL,
 product_timestamp TIMESTAMP NOT NULL,
 UNIQUE (urn),
 FOREIGN KEY (md_id) REFERENCES product_metadata(id)
);
