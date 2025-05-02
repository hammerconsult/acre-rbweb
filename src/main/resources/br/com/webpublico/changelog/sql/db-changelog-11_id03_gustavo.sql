update divida set divida.tipocadastro = (select tipocadastro.tipocadastrotributario from tipocadastro where tipocadastro.id = divida.tipocadastro_id);
update divida set divida.tipocadastro_id = null;