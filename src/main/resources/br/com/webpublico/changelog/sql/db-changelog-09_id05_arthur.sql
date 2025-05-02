create or replace function filhoNaVigencia(inicioFilho IN date, finalFilho IN date, inicioPai IN date, finalPai IN date) 
return int is
begin
  if (inicioFilho is null or inicioPai is null) then return 0; end if;
  if (finalFilho is null and finalPai is null) then return 1; end if;
  --Pai sem final, filho com final após início do pai
  if (finalPai is null and finalFilho is not null and finalFilho >= inicioPai) then return 1; end if;
  --Pai com final e filho sem final mas com início anterior ao final do pai
  if (finalPai is not null and finalFilho is null and inicioFilho <= finalPai) then return 1; end if;
  --Pai e filho com final, e filho iniciando ou terminando na vigência do pai
  if (finalPai is not null and finalFilho is not null and ((inicioFilho >= inicioPai and inicioFilho <= finalPai) or (finalFilho>=inicioPai and finalFilho <=finalPai))) then return 1; else return 0; end if; 
  return 0;
end filhoNaVigencia;
