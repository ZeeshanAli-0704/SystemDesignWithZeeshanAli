package org.example;

public class LazyApproach {

    private static LazyApproach lazyApproach;

    public static LazyApproach getLazyApproach(){

        if(lazyApproach == null){
            lazyApproach = new LazyApproach();
            return lazyApproach;
        }

        return lazyApproach;
    };

}
