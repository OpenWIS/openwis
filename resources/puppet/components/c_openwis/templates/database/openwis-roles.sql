drop role if exists openwis;
create role openwis nosuperuser nocreatedb nocreaterole password '<%= @database_password %>' login;
grant create on database "OpenWIS" to openwis;
grant select,insert,update,delete,references on geography_columns, geometry_columns, spatial_ref_sys to openwis;
