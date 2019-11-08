-- ALTER TABLE Settings ALTER COLUMN value longvarchar;
-- Mckoi only
ALTER CREATE TABLE Settings
	(  id        int,
    parentId  int,
    name      varchar(32)    not null,
    value     longvarchar);
    
ALTER TABLE Metadata ADD displayorder int;

INSERT INTO Settings VALUES (90,1,'selectionmanager',NULL);
INSERT INTO Settings VALUES (91,90,'maxrecords','1000');
INSERT INTO Settings VALUES (210,1,'localrating',NULL);
INSERT INTO Settings VALUES (211,210,'enable','false');
INSERT INTO Settings VALUES (220,1,'downloadservice',NULL);
INSERT INTO Settings VALUES (221,220,'leave','false');
INSERT INTO Settings VALUES (222,220,'simple','true');
INSERT INTO Settings VALUES (223,220,'withdisclaimer','false');
INSERT INTO Settings VALUES (230,1,'xlinkResolver',NULL);
INSERT INTO Settings VALUES (231,230,'enable','false');
INSERT INTO Settings VALUES (600,1,'indexoptimizer',NULL);
INSERT INTO Settings VALUES (601,600,'enable','true');
INSERT INTO Settings VALUES (602,600,'at',NULL);
INSERT INTO Settings VALUES (603,602,'hour','0');
INSERT INTO Settings VALUES (604,602,'min','0');
INSERT INTO Settings VALUES (605,602,'sec','0');
INSERT INTO Settings VALUES (606,600,'interval',NULL);
INSERT INTO Settings VALUES (607,606,'day','0');
INSERT INTO Settings VALUES (608,606,'hour','24');
INSERT INTO Settings VALUES (609,606,'min','0');
INSERT INTO Settings VALUES (720,1,'inspire',NULL);
INSERT INTO Settings VALUES (721,720,'enable','false');
INSERT INTO Settings VALUES (722,1,'cache',NULL);
INSERT INTO Settings VALUES (723,722,'enable','false');

UPDATE Settings SET value='2.5.0' WHERE name='version';
UPDATE Settings SET value='UNSTABLE' WHERE name='subVersion';