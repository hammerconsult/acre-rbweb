update ResponsabilidadeVTB resp set resp.exercicio_id = (select id from exercicio where ano = extract(year from resp.DATAREGISTRO))
