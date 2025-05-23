update eventobem set valordolancamento = 0 where valordolancamento is null;

update eventobem set datalancamento = dataoperacao
where tipoeventobem = 'ITEMLOTESOLICITACAOALIENACAO';
