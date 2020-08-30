package com.bikash.bikashBackend.util;

/*##jokhon user agent er kache cash out korbe
        #Agent
                                                                agent passe proti 20 takai  = 2.5
                                                                agent passe proti takai = 0.0025 final
logic =
        - first check user have enough amount or not.
        - if true, cashout user to agent.
        #jokhon agnet admin er kache cashout korbe tokhon
        - - agent passe proti takai = 0.0025.tk ei taka ta agent er new ekta
        earn name e table e add hobe
        - - badbaki taka admin er kache chole jabe.

        *****************************************************************
        optional:-
        #Bkash ********
                                                                bkash proti takai pabe  =  0.0175 final
                                                                Bkash proti 20 takai pabe  = 17.5*/

public final class CashOutDemandUtil {
    public static final double forAgentPerTakaCommission = 0.0025;//for per taka
    public static final double forAdminPerTakaCommission = 0.0175;//for per taka
}
