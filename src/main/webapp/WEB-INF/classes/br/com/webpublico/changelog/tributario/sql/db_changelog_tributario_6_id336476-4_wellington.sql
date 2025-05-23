update MENSAGEMCONTRIBUSUARIO mcu set lida = 1
where exists (select 1 from MENSAGEMCONTRIBUINTELIDA mcl
              where mcl.MENSAGEMCONTRIBUINTE_ID = mcu.MENSAGEMCONTRIBUINTE_ID
                and mcl.USUARIOWEB_ID = mcu.USUARIOWEB_ID)
