
create or replace 
procedure adicionarecurso(nome_pai in varchar2 , nome_cadastro in varchar2, url in varchar2, grupo_rec in varchar2)is
   total_reg NUMBER(5);
   ultimasequencia NUMBER(10);
   id number(10);
   id_recurosistema number(10);
   pai_id number(10);
   id_gruporec number(10);
BEGIN
   select hibernate_sequence.nextval into id from dual;
   select hibernate_sequence.nextval into id_recurosistema from dual;
   
   ----retorno registro se jaexistir recurso com o mesmo nome
   SELECT 
        count(*) INTO total_reg 
   FROM 
      menu where trim(lower(label))=trim(lower(nome_cadastro));
      
   ----retorna ultima sequencia dos filhos do pai informado
   SELECT  
        coalesce(max(filho.ordem)+10,10) INTO ultimasequencia 
   FROM menu filho 
   inner join menu pai on pai.id=filho.pai_id 
   where trim(lower(pai.label))=trim(lower(nome_pai));
   
      ----retorna id do pai 
   SELECT  
        distinct(pai.id) INTO pai_id 
   FROM menu filho 
   inner join menu pai on pai.id=filho.pai_id 
   where trim(lower(pai.label)) like trim(lower(nome_pai));
   
   
   select id into id_gruporec from gruporecurso where lower(trim(nome))=lower(grupo_rec);
   
   IF total_reg > 1  THEN  -- check quantity      
       dbms_output.put_line('----------------------------------------------');
       dbms_output.put_line(' O label para o recurso cadastrado já existe  ');
       dbms_output.put_line('----------------------------------------------');
   END IF;
   
   IF total_reg = 0  THEN  -- check quantity      
       dbms_output.put_line('Insert into menu (ID,LABEL,CAMINHO,PAI_ID,ORDEM) values ('||id||','''||nome_cadastro||''','''||url||''','||pai_id||','||ultimasequencia||')');       
       dbms_output.put_line('Insert into recursosistema (ID,NOME,CAMINHO,CADASTRO,MODULO) values ('||id_recurosistema||','''||REPLACE(replace(url,'/',' - '),'.xhtml','')||''','''||url||''','||0||','''||null||''')');
       dbms_output.put_line('insert into gruporecursosistema(RECURSOSISTEMA_ID,GRUPORECURSO_ID) values('||id_recurosistema||','||id_gruporec||')');
       Insert into menu (ID,LABEL,CAMINHO,PAI_ID,ORDEM) values (id,nome_cadastro,url,pai_id,ultimasequencia);       
       Insert into recursosistema (ID,NOME,CAMINHO,CADASTRO,MODULO) values (id_recurosistema,REPLACE(replace(url,'/',' - '),'.xhtml',''),url,0,null);
       insert into gruporecursosistema(RECURSOSISTEMA_ID,GRUPORECURSO_ID) values(id_recurosistema,id_gruporec);
       commit;
       dbms_output.put_line('----------------------------------------------');
       dbms_output.put_line('                    Sucesso                   ');
       dbms_output.put_line('----------------------------------------------');
   END IF;
END;