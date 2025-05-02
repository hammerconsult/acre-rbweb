update calculoiss iss set iss.CALCULOISSESTORNO_ID = (select estorno.id from CalculoISSEstorno estorno where estorno.CALCULOISS_ID = iss.id) where iss.CALCULOISSESTORNO_ID is null
