update eventofp
set FORMULA = 'if(calculador.mesesTrabalhadosPorAno() <= 0){
return 0;
}' ||
              '' || formula
where CODIGO = '124'
  and formula not like '%calculador.mesesTrabalhadosPorAno%'
