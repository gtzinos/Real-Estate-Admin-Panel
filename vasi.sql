-- PG ADMIN POSTGRES






--drop the database
	create or replace function dropdb()
		returns void AS
		$$
	begin
		IF EXISTS (
				SELECT *
				FROM   pg_catalog.pg_tables 
				WHERE  schemaname = 'db080'
				AND    tablename  = 'houses'
				) THEN
			   execute 'drop table houses';
		end if;
		
		IF EXISTS (
				SELECT *
				FROM   pg_catalog.pg_tables 
				WHERE  schemaname = 'db080'
				AND    tablename  = 'mesites'
				) THEN
			   execute 'drop table mesites';
		end if;
		RAISE NOTICE 'Database dropped succefully !!!';
	END;		
	$$ LANGUAGE plpgsql;

--create the database
	create or replace function createdb()
		returns void AS
		$$
	BEGIN	
			IF NOT EXISTS (
				SELECT *
				FROM   pg_catalog.pg_tables 
				WHERE  schemaname = 'db080'
				AND    tablename  = 'mesites'
				) THEN
				
			   EXECUTE '
					CREATE TABLE mesites (
						  at varchar(255) NOT NULL UNIQUE PRIMARY KEY,
						  name varchar(255) NOT NULL,
						  surname varchar(255) NOT NULL,
						  phone BIGINT NOT NULL,
						  afm varchar(255) NOT NULL UNIQUE,
						  odos varchar(255) NOT NULL
					);
				';				
			END IF;
		
			
		
			IF NOT EXISTS (
				SELECT *
				FROM   pg_catalog.pg_tables 
				WHERE  schemaname = 'db080'
				AND    tablename  = 'houses'
				) THEN
				
			   EXECUTE ' 
					  CREATE TABLE houses (
						  id SERIAL NOT NULL PRIMARY KEY,
						  tm double precision NOT NULL,
						  region varchar(255) NOT NULL,
						  odos varchar(255) NOT NULL,
						  enoikiasi int NOT NULL,
						  polisi int NOT NULL,
						  domatia int NOT NULL,
						  xronia int NOT NULL,
						  mesitis varchar(255) NOT NULL constraint h_mesitis REFERENCES mesites(at) ON DELETE SET NULL ON UPDATE CASCADE
					  ); 
				';
			END IF;
			RAISE NOTICE 'Database created succefully !!!';
	END;	
	$$ LANGUAGE plpgsql;

	
	--insert one house into database
create or replace function insertSpiti(tm double precision,region varchar(255),odos varchar(255),enoikiasi int,polisi int,domatia int,xronia int,mesitis varchar(255))
		returns text AS
		$$
		declare
		house_id text := -1;
	BEGIN
	RAISE NOTICE 'Database created succefully !!!';
		IF EXISTS (
				SELECT *
				FROM   pg_catalog.pg_tables 
				WHERE  schemaname = 'db080'
				AND    tablename  = 'houses'
				) THEN
			  execute ' insert into houses(tm,region,odos,enoikiasi,polisi,domatia,xronia,mesitis) values( ' || tm || ',''' || region || ''',''' || odos || ''',' || enoikiasi || ',' || polisi || ',' || domatia || ',' || xronia || ',''' || mesitis || ''')';
			  SELECT into house_id currval(pg_get_serial_sequence('houses','id'));
			  return house_id;
		else
			return house_id;
		end if;
	END;	
$$ LANGUAGE plpgsql;
	
	--insert one mesitis into database
	create or replace function insertMesitis(at varchar(255),name varchar(255),surname varchar(255),phone bigint,afm varchar(255),odos varchar(255))
		returns text AS
		$$
	BEGIN
		IF EXISTS (
				SELECT *
				FROM   pg_catalog.pg_tables 
				WHERE  schemaname = 'db080'
				AND    tablename  = 'mesites'
				) THEN
			   insert into mesites values(at,name,surname,phone,afm,odos);
			   return 'Ok';
		else
			return 'No Table';
		end if;
	END;	
$$ LANGUAGE plpgsql;
	
	-- delete ena spiti me xrisi pedio mesitis at
	create or replace function deleteHouseUseMesitis(mesitis_at varchar(255))
		returns void AS
		$$
	begin
		
	execute 'delete from houses where mesitis=''' || mesitis_at || '''';

	END;		
	$$ LANGUAGE plpgsql;
	
-- delete ena spiti me xrisi pedio house id
create or replace function deleteHouseUseId(house_id integer)
		returns void AS
		$$
	begin
		
	execute 'delete from houses where id =''' || house_id || '''';

	END;		
	$$ LANGUAGE plpgsql;

-- delete mesiti me xrisi arithmo tautotitas at
create or replace function deleteMesiti(mesitis_at varchar(255))
		returns void AS
		$$
	begin
		
	execute 'delete from mesites where at=''' || mesitis_at || '''';

	END;		
	$$ LANGUAGE plpgsql;
	
--enimerwnei ton pinaka houses
	create or replace function updateHouse(tm double precision,region varchar(255),odos varchar(255),enoikiasi int,polisi int,domatia int,xronia int,mesitis varchar(255),search_id integer)
		returns void AS
		$$
	begin
		
	execute 'update houses set tm=''' || tm || ''' , region=''' || region || ''' , odos=''' || 
				odos || ''' , enoikiasi=''' || enoikiasi || ''' , polisi=''' || polisi 
				|| ''' , domatia=''' || domatia || ''' , xronia=''' || xronia || ''' , mesitis=''' || mesitis 
				|| ''' where id=''' || search_id || '''';
	

	END;		
	$$ LANGUAGE plpgsql;

-- enimerwnei ton pinaka mesites
	create or replace function updateMesites(at varchar,name varchar,surname varchar,phone BIGINT,afm varchar,odos varchar,find_at varchar)
		returns void AS
		$$
	begin
		
	execute 'update mesites set at=''' || at || ''' , name=''' || name || ''' , surname=''' || 
				surname || ''' , phone=''' || phone || ''' , afm=''' || afm 
				|| ''' , odos=''' || odos || ''' where at=''' || find_at || '''';

	END;		
	$$ LANGUAGE plpgsql;
	
	
	--epistrefei olous tous mesites
	CREATE OR REPLACE FUNCTION mesitesList()
		RETURNS TABLE(at varchar,name varchar,surname varchar,phone BIGINT,afm varchar,odos varchar)  AS $$
	BEGIN
			RETURN QUERY SELECT * FROM mesites;
	END;
$$ LANGUAGE plpgsql;

-- epistrefei ola ta spitia
CREATE OR REPLACE FUNCTION housesList()
		RETURNS TABLE(id integer,tm double precision,region varchar,odos varchar,enoikiasi int,polisi int,domatia int,xronia int,mesitis varchar)  AS $$
	BEGIN
			RETURN QUERY SELECT * FROM houses;
	END;
$$ LANGUAGE plpgsql;


--custom search me pedia
CREATE OR REPLACE FUNCTION customHouseSearch(tm_from double precision,tm_mexri double precision,domatia_from integer,domatia_mexri integer,xronia_from integer,xronia_mexri integer)
		RETURNS TABLE(id integer,tm double precision,region varchar,odos varchar,enoikiasi int,polisi int,domatia int,xronia int,mesitis varchar)  AS $$
	BEGIN
			RETURN QUERY execute ' SELECT * FROM houses where  tm >= ' || tm_from || ' and tm <= ' || tm_mexri || ' and domatia >= ' || domatia_from || ' and domatia <= ' || domatia_mexri || ' and 
			xronia >= ' || xronia_from || ' and xronia <= ' || xronia_mexri;
	END;
$$ LANGUAGE plpgsql;

--search spitiou me vasi kapoion mesiti
CREATE OR REPLACE FUNCTION meVasiMesitiHouseSearch(mesitis_at varchar(255))
		RETURNS TABLE(id integer,tm double precision,region varchar,odos varchar,enoikiasi int,polisi int,domatia int,xronia int,mesitis varchar)  AS $$
	BEGIN
			RETURN QUERY execute ' SELECT * FROM houses where  mesitis = ''' || mesitis_at || '''';
	END;
$$ LANGUAGE plpgsql;





-- ORACLE VASI

 

 create or replace PROCEDURE dropdb(tables_found out number) is
  
BEGIN  

 execute immediate 'SELECT count(*)
                      FROM user_tables
                        where table_name=''AGORES''' into tables_found;

  if(tables_found = 1) then 
    execute immediate 'drop table AGORES';
  end if;
  
 execute immediate 'SELECT count(*)
                      FROM user_tables
                        where table_name=''ENOIKIASEIS''' into tables_found;

  if(tables_found = 1) then 
    execute immediate 'drop table ENOIKIASEIS';
  end if;
  
   execute immediate 'SELECT count(*)
                      FROM user_tables
                        where table_name=''PELATES''' into tables_founD;

  if(tables_found = 1) then 
    execute immediate 'drop table PELATES';
  end if;
  
 DBMS_OUTPUT.PUT_LINE('Oracle Database deleted successfully !!!');

EXCEPTION
    WHEN OTHERS THEN      
          DBMS_OUTPUT.PUT_LINE('Error we when try to delete the database.One or more tables doesnt exist on our database !!!');
          
 end dropdb;
 

 

create or replace PROCEDURE createdb(tables_found out number) IS
BEGIN

   execute immediate 'SELECT count(*)
                      FROM user_tables
                        where table_name=''PELATES''' into tables_founD;

  if(tables_found = 0) then 
    execute immediate '
          CREATE TABLE Pelates (
            at varchar(255) NOT NULL PRIMARY KEY,
            name varchar(255) NOT NULL,
            surname varchar(255) NOT NULL,
            phone long NOT NULL,
            afm varchar(255) NOT NULL UNIQUE,
            odos varchar(255) NOT NULL
          )
        ';		
  end if;


  execute immediate 'SELECT count(*)
                      FROM user_tables
                        where table_name=''AGORES''' into tables_found;

  if(tables_found = 0) then 
    execute immediate '
          CREATE TABLE Agores (
            pelatis varchar(255) constraint a_mesitis REFERENCES Pelates(at) ON DELETE SET NULL,
            spiti int NOT NULL,
            kostos real not null,
			iban varchar(255) NOT NULL,
            imera timestamp not null
          )
				';
  end if;
  
 execute immediate 'SELECT count(*)
                      FROM user_tables
                        where table_name=''ENOIKIASEIS''' into tables_found;

  if(tables_found = 0) then 
    execute immediate '
            CREATE TABLE Enoikiaseis (
              pelatis varchar(255) constraint e_mesitis REFERENCES Pelates(at) ON DELETE SET NULL,
              spiti int NOT NULL,
              poso_mina real not null,
			  iban varchar(255) not null,
              imera timestamp not null
            )
          ';
  end if;
  
	
 -- an uparxei pinakas endiaferon
 
 execute immediate 'SELECT count(*)
                      FROM user_tables
                        where table_name=''ENDIAFERON''' into tables_founD;

  if(tables_found = 0) then 
    execute immediate '
          CREATE TABLE ENDIAFERON (
			  pelatis varchar(255) constraint end_mesitis REFERENCES Pelates(at) ON DELETE CASCADE,
			  spiti int NOT NULL,
			  imera timestamp not null,
			  primary key(pelatis,spiti)
          )
        ';		
  end if;
  
 DBMS_OUTPUT.PUT_LINE('Oracle Database created successfully !!!');

EXCEPTION
    WHEN OTHERS THEN      
          DBMS_OUTPUT.PUT_LINE('Error we when try to create the database.One or more tables already exist on our database !!!');

  END createdb;

 -- Methodos poy eisagei enan pelati sthn vasi dedomenwn 
 
create or replace function insertPelatis (art in varchar,onoma in varchar,surname in varchar,arithmos in long,afm in varchar,odos in varchar)
return varchar is
tables_found integer;
BEGIN  
  
  execute immediate 'SELECT count(*)
                      FROM user_tables
                        where table_name=''PELATES''' into tables_found;

  if(tables_found = 1) then 
    insert into pelates values(art,onoma,surname,arithmos,afm,odos);
    return 'Ok';
  else
    return 'No Table';
  end if;

 end insertPelatis;
 
 
   -- Methodos poy eisagei ena endiaferon spitiou sthn vasi dedomenwn 
create or replace PROCEDURE insertEndiaferon (art in varchar,house in integer) is
BEGIN   
   execute immediate ' insert into Endiaferon(pelatis,spiti,imera) values(''' || art || ''',' || house || ',CURRENT_TIMESTAMP) ';
end insertEndiaferon;
 
 
  -- Methodos poy eisagei mia enoikiasi spitiou sthn vasi dedomenwn 
create or replace PROCEDURE insertEnoikiasi (art in varchar,house in integer,poso in real,iban in varchar) is
BEGIN   
   execute immediate ' insert into enoikiaseis(pelatis,spiti,poso_mina,iban,imera) values(''' || art || ''',' || house || ',' || poso || ',''' || iban || ''',CURRENT_TIMESTAMP) ';
end insertEnoikiasi;

 -- Methodos poy eisagei mia agora spitiou sthn vasi dedomenwn 
create or replace PROCEDURE insertAgores (art in varchar,house in integer,poso in real,iban in varchar) is
BEGIN   
   execute immediate ' insert into agores(pelatis,spiti,kostos,iban,imera) values(''' || art || ''',' || house || ',' || poso || ',''' || iban || ''',CURRENT_TIMESTAMP) ';
end insertAgores;
 
 
 -- variable type object  pelatis ousiastika to opoio exei ola ta pedia tou.sigekrimena to CLOB einai to long
 create type pelatis_obj is object (art varchar(255),onoma varchar(255),surname varchar(255),arithmos CLOB,afm varchar(255),odos varchar(255));
 
-- table tipou object pelatis_obj opou tha exei pelates mesa
create type pelatis_tab is table of pelatis_obj;

 --methodos pou epistrefe i prosoxh sto pinaka pelatis_tab olous tous pelates kai emeis prepei na ta kanoume apo kei select * from table(pelatesList()) sthn java
create or replace function pelatesList
return pelatis_tab is
  var_tab pelatis_tab := pelatis_tab();
  n integer := 0;
BEGIN
-- at as art logw oti de mporw na kanw xrhsh tou r.at katw gia kompleksikous logous ths oracle !!!
  for r in (Select at as art,name,surname,phone,afm,odos from pelates)
  loop
    var_tab.extend;
    n := n+1;
    var_tab(n) := pelatis_obj(r.art,r.name,r.surname,r.phone,r.afm,r.odos);
  end loop;
  return var_tab;

 end pelatesList;
 
 -- variable type object  enoikiaseis ousiastika to opoio exei ola ta pedia tou.
 create or replace type enoikiaseis_obj is object (pelatis varchar(255),spiti int,poso_mina real,iban varchar(255),imera timestamp);
 
-- table tipou object enoikiaseis_obj opou tha exei enoikiaseis mesa
create type enoikiaseis_tab is table of enoikiaseis_obj;


 --methodos pou epistrefe i prosoxh sto pinaka enoikiaseis_tab oles tis enoikiaseis kai emeis prepei na ta kanoume apo kei select * from table(enoikiaseisList()) sthn java
create or replace function enoikiaseisList
return enoikiaseis_tab is
  var_tab enoikiaseis_tab := enoikiaseis_tab();
  n integer := 0;
BEGIN
  for r in (Select * from enoikiaseis)
  loop
    var_tab.extend;
    n := n+1;
    var_tab(n) := enoikiaseis_obj(r.pelatis,r.spiti,r.poso_mina,r.iban,r.imera);
  end loop;
  return var_tab;

 end enoikiaseisList;
 
 
 -- variable type object  endiaferon ousiastika to opoio exei ola ta pedia tou.
 create or replace type endiaferon_obj is object (pelatis varchar(255),spiti int,imera timestamp);
 
-- table tipou object endiaferon_obj opou tha exei endiaferontes mesa
create type endiaferon_tab is table of endiaferon_obj;


 --methodos pou epistrefe i prosoxh sto pinaka endiaferon olous tous endiaferomenous kai emeis prepei na ta kanoume apo kei select * from table(endiaferonList()) sthn java
create or replace function endiaferonList(house int)
return endiaferon_tab is
  var_tab endiaferon_tab := endiaferon_tab();
  n integer := 0;
BEGIN
  for r in (Select * from endiaferon where spiti=house)
  loop
    var_tab.extend;
    n := n+1;
    var_tab(n) := endiaferon_obj(r.pelatis,r.spiti,r.imera);
  end loop;
  return var_tab;

 end endiaferonList;
 
 
 
 -- variable type object  agores ousiastika to opoio exei ola ta pedia tou.
 create or replace type agores_obj is object (pelatis varchar(255),spiti int,kostos real,iban varchar(255),imera timestamp);
 
-- table tipou object agores_obj opou tha exei agores mesa
create type agores_tab is table of agores_obj;


 --methodos pou epistrefe i prosoxh sto pinaka agores_tab oles tis agores kai emeis prepei na ta kanoume apo kei select * from table(agoresList()) sthn java
create or replace function agoresList
return agores_tab is
  var_tab agores_tab := agores_tab();
  n integer := 0;
BEGIN
  for r in (Select * from agores)
  loop
    var_tab.extend;
    n := n+1;
    var_tab(n) := agores_obj(r.pelatis,r.spiti,r.kostos,r.iban,r.imera);
  end loop;
  return var_tab;

 end agoresList;
 
 
 --methodos poy enimerwnei ton pinaka pelates
  create or replace PROCEDURE updatePelates(art varchar,name varchar,surname varchar,phone long,afm varchar,odos varchar,find_at varchar) is
  
BEGIN  

 execute immediate 'update pelates set at=''' || art || ''' , name=''' || name || ''' , surname=''' || 
				surname || ''' , phone=''' || phone || ''' , afm=''' || afm 
				|| ''' , odos=''' || odos || ''' where at=''' || find_at || '''';
          
 end updatePelates;

 

 
  --methodos poy enimerwnei ton pinaka endiaferon
  create or replace PROCEDURE updateEndiaferon(art in varchar,house in integer,old_art in varchar) is
  
BEGIN  

 execute immediate 'update Endiaferon set pelatis=''' || art || '''' || ' where spiti=' || house || ' and pelatis=''' || old_art || '''';
          
 end updateEndiaferon;
 
  --methodos poy enimerwnei ton pinaka enoikiaseis
  create or replace PROCEDURE updateEnoikiaseis(house integer,poso_mina real,iban varchar) is
  
BEGIN  

 execute immediate 'update enoikiaseis set poso_mina=' || poso_mina || ' , iban=''' || iban || ''' where spiti=' || house;
          
 end updateEnoikiaseis;
 
 
  --methodos poy enimerwnei ton pinaka agores
  create or replace PROCEDURE updateAgores(house integer,kostos real,iban varchar) is
  
BEGIN  

 execute immediate 'update agores set kostos=' || kostos || ' , iban=''' || iban || ''' where spiti=' || house;
          
 end updateAgores;
 
 
 --methodos poy diagrafei enan pelati apo ton pinaka pelates
  create or replace PROCEDURE deletePelatis(art varchar) is
  
BEGIN  

 execute immediate 'delete from pelates where at=''' || art || '''';
          
 end deletePelatis;
 
 
 
  --methodos poy diagrafei mia enoikiasi apo ton pinaka enoikiaseis
  create or replace PROCEDURE deleteEnoikiasi(house integer) is
  
BEGIN  

 execute immediate 'delete from enoikiaseis where spiti=' || house;
          
 end deleteEnoikiasi;
 
 
  --methodos poy diagrafei mia agora apo ton pinaka agores
  create or replace PROCEDURE deleteAgora(house integer) is
  
BEGIN  

 execute immediate 'delete from agores where spiti=' || house;
         
 end deleteAgora;
 
  --methodos poy diagrafei ena endiaferon apo ton pinaka endiaferon
  create or replace PROCEDURE deleteEndiaferonUseAll(art varchar,house integer) is
  
BEGIN  

 execute immediate 'delete from Endiaferon where spiti=' || house || ' and pelatis=''' || art || '''';
         
 end deleteEndiaferonUseAll;
 
   --methodos poy diagrafei ena endiaferon apo ton pinaka endiaferon
  create or replace PROCEDURE deleteEndiaferonUseArt(art varchar) is
  
BEGIN  

 execute immediate 'delete from Endiaferon where pelatis=''' || art || '''';
         
 end deleteEndiaferonUseArt;
 
   --methodos poy diagrafei ena endiaferon apo ton pinaka endiaferon
  create or replace PROCEDURE deleteEndiaferonUseHouse(house integer) is
  
BEGIN  

 execute immediate 'delete from Endiaferon where spiti=' || house;
         
 end deleteEndiaferonUseHouse;


 
