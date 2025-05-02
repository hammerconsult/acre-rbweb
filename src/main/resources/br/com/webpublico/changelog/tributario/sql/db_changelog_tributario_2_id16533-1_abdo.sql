    delete from credencialtrafego ct where  ct.id in (
    select c.id from credencialrbtrans c
    where c.tiporequerente = 'MOTORISTA'
     and exists(select 1 from credencialtrafego ct where ct.id = c.id));

    delete from credencialrbtrans c
    where  not exists(select 1 from credencialtrafego ct where ct.id = c.id)
    and not exists(select 1 from credencialtransporte ct where ct.id = c.id);
