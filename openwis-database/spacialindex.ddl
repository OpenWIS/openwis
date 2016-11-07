-- Clean
DELETE FROM geometry_columns WHERE f_table_name='spatialindex'; 
DROP TABLE IF EXISTS SpatialIndex;

-- Create table
CREATE TABLE spatialindex (fid int, _uuid varchar(250), primary key(fid));

-- Create spatialIndexNDX1
CREATE INDEX spatialIndexNDX1 ON spatialindex(_uuid);

-- Define geometry column
SELECT AddGeometryColumn('spatialindex', 'geo', 4326, 'MULTIPOLYGON', 2 );

-- Create spatialIndexNDX2
CREATE INDEX spatialIndexNDX2 ON spatialindex USING GIST(geo);
