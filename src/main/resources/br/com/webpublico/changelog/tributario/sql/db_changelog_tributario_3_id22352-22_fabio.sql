update processoparcelamento pp set pp.VALORDESCONTOIMPOSTO = 0 where pp.VALORDESCONTOIMPOSTO < 0;
update processoparcelamento pp set pp.VALORDESCONTOTAXA = 0 where pp.VALORDESCONTOTAXA < 0;
update processoparcelamento pp set pp.VALORDESCONTOJUROS = 0 where pp.VALORDESCONTOJUROS < 0;
update processoparcelamento pp set pp.VALORDESCONTOMULTA = 0 where pp.VALORDESCONTOMULTA < 0;
update processoparcelamento pp set pp.VALORDESCONTOCORRECAO = 0 where pp.VALORDESCONTOCORRECAO < 0;
update processoparcelamento pp set pp.VALORDESCONTOHONORARIOS = 0 where pp.VALORDESCONTOHONORARIOS < 0;
