--Comando de insert de RecursoSistema
DECLARE NUM NUMBER(10);
   cursor c1 is
      select nome, caminho, cadastro, modulo from( 
SELECT trim(nome) AS nome, trim(caminho) AS caminho,cadastro,modulo FROM teste.recursosistema
UNION
SELECT trim(nome) AS nome, trim(caminho)as caminho,cadastro,modulo FROM migracao_tributario.recursosistema
UNION
SELECT trim(nome) AS nome, trim(caminho)as caminho,cadastro,modulo FROM migracao_rh.recursosistema
UNION
SELECT trim (nome) AS nome, trim(caminho)as caminho,cadastro,modulo FROM migracao_contabil.recursosistema)
where caminho not in(select x.caminho from recursosistema x);
BEGIN
   FOR linha in c1
   LOOP
      INSERT INTO recursoSistema(ID, nome, caminho, cadastro, modulo) 
      values (hibernate_sequence.nextval, linha.nome, linha.caminho, linha.cadastro, linha.modulo);
      commit;       
   END LOOP;
    dbms_output.put_line('FIM');
    COMMIT;
END;