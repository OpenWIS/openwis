--
-- Purge for the blacklisting:
-- Purge the blacklisting entries for user having no activity (request/subscription)
-- since 30 days (can be changed)
-- 
CREATE OR REPLACE FUNCTION purge_blacklisting() RETURNS TRIGGER AS $purge_blacklisting$
BEGIN
    delete from openwis_blacklist_info 
    where user_id in (
    select user_id
    from openwis_disseminated_data 
    group by user_id
    having date_part('days', now() - max(date)) > 30);
    RETURN NULL;
END;
$purge_blacklisting$ language plpgsql;

CREATE TRIGGER "trg-purge-blacklisting" AFTER INSERT OR UPDATE
   ON openwis_disseminated_data FOR EACH STATEMENT
   EXECUTE PROCEDURE purge_blacklisting();
   
