update reducaovalorbemnaoaplicave rvbna set rvbna.valororiginal = (
select esb.valororiginal
   from eventobem evb
  inner join estadobem esb on esb.id = evb.estadoresultante_id
where evb.bem_id = rvbna.bem_id
  and rownum = 1
);
