update menu set label = 'ALVARÁ DE CONSTRUÇÃO E HABITE-SE'
where id = 10694520970;

update menu set pai_id = 10694521196
where id = 10694521155;

update menu set pai_id = 10694521196
where id = 10694521111;

update menu set pai_id = 10694520970
where id = 10694522003;

update menu set pai_id = 10694520970
where id = 10694522115;

update menu set pai_id = 10694520970
where id = 10694522302;

update menu set pai_id = 10694520970
where id = 10694522373;

delete from menu where id = 10694522172;

delete from menu where id = 10694521928;

update menu set ordem = 0 where id = 10694521111;

update menu set ordem = 5 where id = 10694521155;

update menu set ordem = 0 where id = 10694521196;

update menu set ordem = 5 where id = 10694522003;

update menu set ordem = 10 where id = 10694522302;

update menu set ordem = 15 where id = 10694522373;

update menu set ordem = 30 where id = 10694522115;

update menu set ordem = 100 where id = 10694522410;

update menu set pai_id = 10694521196 where id in (10694521455, 10694521265);

update menu set pai_id = 10694521196 where id in (10694521709,10694521652,10694521845,10694521804);

delete from menu where id in (10694521221, 10694521550);

update menu set ordem = 10 where id = 10694521265;

update menu set ordem = 15 where id = 10694521455;

update menu set ordem = 20 where id = 10694521652;

update menu set ordem = 25 where id = 10694521804;

update menu set ordem = 30 where id = 10694521709;

update menu set ordem = 35 where id = 10694521845;

