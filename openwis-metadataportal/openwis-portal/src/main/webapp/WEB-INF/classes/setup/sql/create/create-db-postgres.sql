-- ======================================================================
-- ===   Sql Script for Database : Geonet
-- ===
-- === Build : 153
-- ======================================================================

CREATE TABLE Relations
  (
    id         int,
    relatedId  int,

    primary key(id,relatedId)
  );

-- ======================================================================

CREATE TABLE Categories
  (
    id    int,
    name  varchar(32)   not null,

    primary key(id),
    unique(name)
  );

-- ======================================================================

CREATE TABLE Settings
  (
    id        int,
    parentId  int,
    name      varchar(32)    not null,
    value     text,

    primary key(id),

    foreign key(parentId) references Settings(id)
  );

-- ======================================================================

CREATE TABLE Languages
  (
    id    varchar(5),
    name  varchar(32)   not null,

    primary key(id)
  );

-- ======================================================================

CREATE TABLE Sources
  (
    uuid     varchar(250),
    name     varchar(250),
    isLocal  char(1)        default 'y',

    primary key(uuid)
  );

-- ======================================================================

CREATE TABLE IsoLanguages
  (
    id    int,
    code  varchar(3)   not null,

    primary key(id),
    unique(code)
  );

-- ======================================================================

CREATE TABLE IsoLanguagesDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references IsoLanguages(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Regions
  (
    id     int,
    north  float   not null,
    south  float   not null,
    west   float   not null,
    east   float   not null,

    primary key(id)
  );

-- ======================================================================

CREATE TABLE RegionsDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Regions(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Users
  (
    id            int,
    username      varchar(128)   not null,
    password      varchar(40)    not null,
    surname       varchar(64),
    name          varchar(64),
    profile       varchar(32)    not null,
    address       varchar(128),
    city          varchar(128),
    state         varchar(32),
    zip           varchar(16),
    country       varchar(128),
    email         varchar(128),
    organisation  varchar(128),
    kind          varchar(16),

    primary key(id),
    unique(username)
  );

-- ======================================================================

CREATE TABLE Operations
  (
    id        int,
    name      varchar(32)   not null,
    reserved  char(1)       default 'n' not null,

    primary key(id)
  );

-- ======================================================================

CREATE TABLE OperationsDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Operations(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Groups
  (
    id           int,
    name         varchar(32)    not null,
    description  varchar(255),
    email        varchar(32),
    referrer     	int,
    reserved  	char(1)       default 'n' not null,
    isGlobal  		char(1)       default 'n' not null,

    primary key(id),
    unique(name, isGlobal),

    foreign key(referrer) references Users(id)
  );

-- ======================================================================

CREATE TABLE GroupsDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Groups(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE UserGroups
  (
    userId   int,
    groupId  int,

    primary key(userId,groupId),

    foreign key(userId) references Users(id),
    foreign key(groupId) references Groups(id)
  );

-- ======================================================================

CREATE TABLE CategoriesDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Categories(id),
    foreign key(langId) references Languages(id)
  );
  
-- ======================================================================

CREATE TABLE DataPolicy
  (
    id           int,
    name         varchar(32)   not null,
    description  varchar(255),

    primary key(id),
    unique(name)
    
  );
  
-- ======================================================================

CREATE TABLE DataPolicyAlias
  (
    id        int,
    name      varchar(32)   not null,
    dpId      int,

    primary key(id),
    
    foreign key(dpId) references DataPolicy(id)
  );
 
-- ======================================================================

CREATE TABLE HarvestingTask
  (
	id int,
	uuid         varchar(250)   not null,
    name     varchar(32)    not null,
    harvestingType     varchar(32)    not null,
    validationMode     varchar(32)    not null,
    isrecurrent     char(1)        default 'n' not null,
    recurrentPeriod     int,
    lastRun   varchar(24),
    backup     varchar(32),
    status     varchar(32)    not null,
    isSynchronization   char(1)        default 'n' not null,
    isIncremental  char(1)        default 'n' not null,
    categoryid int not null,
	startingdate varchar(24),
	
	primary key(id),
	
    foreign key(categoryid) references Categories(id),
	
    unique(uuid)
  );

-- ======================================================================

CREATE TABLE Metadata
  (
    id           int,
    uuid         citext   not null,
    schemaId     varchar(32)    not null,
    isTemplate   char(1)        default 'n' not null,
    isHarvested  char(1)        default 'n' not null,
    createDate   varchar(24)    not null,
    changeDate   varchar(24)    not null,
    data         text           not null,
    source       varchar(250)   not null,
    title        varchar(255),
    root         varchar(255),
    owner        varchar(32)    default null,
    harvestUri   varchar(255)   default null,
    rating       int            default 0 not null,
    popularity   int            default 0 not null,
	displayorder int,
	datapolicy   int not null,
	category     int default null,
    harvestingTask  int   default null,
	localImportDate varchar(24) default null,
	importProcess varchar(24),

    primary key(id),
    unique(uuid),

    foreign key(datapolicy) references DataPolicy(id),
    foreign key(harvestingTask) references HarvestingTask(id),
    foreign key(category) references Categories(id)
  );

CREATE INDEX MetadataNDX1 ON Metadata(uuid);
CREATE INDEX MetadataNDX2 ON Metadata(source);

-- ======================================================================

CREATE TABLE OperationAllowed
  (
    groupId      int,
    datapolicyId   int,
    operationId  int,

    primary key(groupId,datapolicyId,operationId),

    foreign key(groupId) references Groups(id),
    foreign key(datapolicyId) references DataPolicy(id),
    foreign key(operationId) references Operations(id)
  );

-- ======================================================================

CREATE TABLE DeletedMetadata
  (
    id           int,
    uuid         citext   not null,
    schemaId     varchar(32)    not null,
    category     int            not null,
    deletionDate timestamp without time zone DEFAULT timezone('UTC'::text, now()),

    primary key(id),
    unique(uuid,category),
    
    foreign key(category) references Categories(id)
  );

-- ======================================================================

CREATE TABLE HarvestingTaskResult
  (
  	harvestingTaskResultId int,
    dateResult   varchar(24)    not null,
	total int,
	added int,
	updated int,
	unchanged int,
	locallyRemoved int,
	unknownSchema int,
	fail char(1) default 'n',
	badFormat int,
	doesNotValidate int,
	ignored int,
	unexpected int,
    errors text,
	harvestingTaskId int,
	
	primary key(harvestingTaskResultId),
	foreign key(harvestingTaskId) references HarvestingTask(id)
	
  );
  
-- ======================================================================

CREATE TABLE HarvestingTaskConfiguration
  (
  	configurationId int,
    attr   varchar(24)    not null,
	val    varchar(250)    not null,
	harvestingTaskId int,
	
	primary key(configurationId),
	foreign key(harvestingTaskId) references HarvestingTask(id)
	
  );
  
-- ======================================================================
  
  CREATE TABLE ActiveBackup
  (
    deploymentName varchar(250)   not null,

    primary key(deploymentName)
  );
  
-- ======================================================================
  
  CREATE TABLE Availability
  (
    task  varchar(250)   not null,
    state char(1)        default 'n' not null,

    primary key(task)
  );

-- ======================================================================
  
  CREATE TABLE Availability_Statistics
  (
    date varchar(24) default null,
    task  varchar(250)   not null,
    available int,
    notAvailable int,

    primary key(date, task)
  );
  
-- ======================================================================

-- Create sequences for tables.

CREATE SEQUENCE Relations_seq;
CREATE SEQUENCE Categories_seq;
CREATE SEQUENCE CategoriesDes_seq;
CREATE SEQUENCE Users_seq;
CREATE SEQUENCE Groups_seq;
CREATE SEQUENCE GroupsDes_seq;
CREATE SEQUENCE DataPolicy_seq;
CREATE SEQUENCE DataPolicyAlias_seq;
CREATE SEQUENCE Metadata_seq;
CREATE SEQUENCE HarvestingTask_seq;
CREATE SEQUENCE HarvestingTaskResult_seq;
CREATE SEQUENCE HarvestingTaskConfiguration_seq;
CREATE SEQUENCE DeletedMetadata_seq;




