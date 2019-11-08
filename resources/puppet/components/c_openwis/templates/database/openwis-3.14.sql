alter table openwis_product_metadata alter column urn type citext;

create table OPENWIS_USER_ALARM (
   ALARM_ID int8 not null,
   DATE timestamp,
   USER_ID varchar(255),
   REQ_TYPE varchar(255),
   REQ_ID int8,
   PROCESSED_REQ_ID int8,
   MESSAGE varchar(255),
   primary key (ALARM_ID)
);

create sequence USER_ALARM_SEQ;
create index USER_ID_AND_REQ_TYPE_IDX on OPENWIS_USER_ALARM (user_id, req_type);
