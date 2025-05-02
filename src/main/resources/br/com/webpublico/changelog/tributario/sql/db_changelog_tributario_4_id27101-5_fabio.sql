update modelo set marca_id = 194848030 where marca_id in (194848028,194848023,768986714) and exists (select id from marca where id = 194848030);
delete from marca where id in (194848028,194848023,768986714);
