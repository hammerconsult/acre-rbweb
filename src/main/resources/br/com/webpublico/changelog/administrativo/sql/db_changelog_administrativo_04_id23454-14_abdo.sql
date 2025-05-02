update msglogreducaoouestorno msg set msg.valor = (select esb.valororiginal
   from eventobem evb
  inner join estadobem esb on esb.id = evb.estadoresultante_id
where evb.bem_id = msg.bem_id
  and rownum = 1);
