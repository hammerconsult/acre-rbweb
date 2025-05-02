
  insert into subcontauniorg(id,unidadeorganizacional_id,subconta_id,exercicio_id)
  select
       hibernate_sequence.nextval as id_nn,       
       ho_14.subordinada_id AS UND_2014, 
       s.id as subconta_id,       
       (select id from exercicio where ano = 2014)ex_2014       
  from subconta s
  inner join subcontafonterec sf on sf.subconta_id=s.id
  inner join fontederecursos ft on ft.id =sf.fontederecursos_id
  inner join subcontauniorg so on so.subconta_id=s.id and so.exercicio_id=ft.exercicio_id
  inner join hierarquiaorganizacional ho on ho.subordinada_id=so.unidadeorganizacional_id and ho.tipohierarquiaorganizacional='ORCAMENTARIA' AND ho.iniciovigencia between '01/01/2013' AND '31/12/2013'
  left join uniorc2013_2014 DP ON dp.ho_id_2013=HO.ID
  left JOIN hierarquiaorganizacional ho_14 ON HO_14.ID=dp.ho_id_2014
  inner join exercicio ex on ex.id=ft.exercicio_id
  left JOIN fontederecursos FT_14 on ft_14.codigo=ft.codigo
  where ex.ano=2013 
  and ft_14.exercicio_id=(select id from exercicio where ano = 2014)