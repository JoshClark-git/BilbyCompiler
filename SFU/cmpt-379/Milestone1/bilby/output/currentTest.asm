        DLabel       $frame-pointer            
        DataI        0                         
        PushD        $frame-pointer            
        Memtop                                 
        StoreI                                 
        DLabel       $stack-pointer            
        DataI        0                         
        PushD        $stack-pointer            
        Memtop                                 
        StoreI                                 
        Jump         $$main                    
        DLabel       $eat-location-zero        
        DataZ        8                         
        DLabel       $print-format-char        
        DataC        37                        %% "%c"
        DataC        99                        
        DataC        0                         
        DLabel       $print-format-string      
        DataC        37                        %% "%s"
        DataC        115                       
        DataC        0                         
        DLabel       $print-format-integer     
        DataC        37                        %% "%d"
        DataC        100                       
        DataC        0                         
        DLabel       $print-format-array       
        LoadI                                  
        DataC        37                        %% "%d"
        DataC        100                       
        DataC        0                         
        DLabel       $print-format-float       
        DataC        37                        %% "%f"
        DataC        102                       
        DataC        0                         
        DLabel       $print-format-boolean     
        DataC        37                        %% "%s"
        DataC        115                       
        DataC        0                         
        DLabel       $print-format-newline     
        DataC        10                        %% "\n"
        DataC        0                         
        DLabel       $print-format-space       
        DataC        32                        %% " "
        DataC        0                         
        DLabel         $print-format-tab       
        DataC        9                         %% "\t"
        DataC        0                         
        DLabel       $boolean-true-string      
        DataC        116                       %% "true"
        DataC        114                       
        DataC        117                       
        DataC        101                       
        DataC        0                         
        DLabel       $boolean-false-string     
        DataC        102                       %% "false"
        DataC        97                        
        DataC        108                       
        DataC        115                       
        DataC        101                       
        DataC        0                         
        DLabel       $errors-general-message   
        DataC        82                        %% "Runtime error: %s\n"
        DataC        117                       
        DataC        110                       
        DataC        116                       
        DataC        105                       
        DataC        109                       
        DataC        101                       
        DataC        32                        
        DataC        101                       
        DataC        114                       
        DataC        114                       
        DataC        111                       
        DataC        114                       
        DataC        58                        
        DataC        32                        
        DataC        37                        
        DataC        115                       
        DataC        10                        
        DataC        0                         
        Label        $$general-runtime-error   
        PushD        $errors-general-message   
        Printf                                 
        Halt                                   
        DLabel       $errors-int-divide-by-zero 
        DataC        105                       %% "integer divide by zero"
        DataC        110                       
        DataC        116                       
        DataC        101                       
        DataC        103                       
        DataC        101                       
        DataC        114                       
        DataC        32                        
        DataC        100                       
        DataC        105                       
        DataC        118                       
        DataC        105                       
        DataC        100                       
        DataC        101                       
        DataC        32                        
        DataC        98                        
        DataC        121                       
        DataC        32                        
        DataC        122                       
        DataC        101                       
        DataC        114                       
        DataC        111                       
        DataC        0                         
        Label        $$i-divide-by-zero        
        PushD        $errors-int-divide-by-zero 
        Jump         $$general-runtime-error   
        DLabel       $errors-float-divide-by-zero 
        DataC        102                       %% "floating divide by zero"
        DataC        108                       
        DataC        111                       
        DataC        97                        
        DataC        116                       
        DataC        105                       
        DataC        110                       
        DataC        103                       
        DataC        32                        
        DataC        100                       
        DataC        105                       
        DataC        118                       
        DataC        105                       
        DataC        100                       
        DataC        101                       
        DataC        32                        
        DataC        98                        
        DataC        121                       
        DataC        32                        
        DataC        122                       
        DataC        101                       
        DataC        114                       
        DataC        111                       
        DataC        0                         
        Label        $$f-divide-by-zero        
        PushD        $errors-float-divide-by-zero 
        Jump         $$general-runtime-error   
        DLabel       $errors-Array-Alloc-Negative 
        DataC        110                       %% "negative array alloc"
        DataC        101                       
        DataC        103                       
        DataC        97                        
        DataC        116                       
        DataC        105                       
        DataC        118                       
        DataC        101                       
        DataC        32                        
        DataC        97                        
        DataC        114                       
        DataC        114                       
        DataC        97                        
        DataC        121                       
        DataC        32                        
        DataC        97                        
        DataC        108                       
        DataC        108                       
        DataC        111                       
        DataC        99                        
        DataC        0                         
        Label        $$neg-alloc-array         
        PushD        $errors-Array-Alloc-Negative 
        Jump         $$general-runtime-error   
        DLabel       $errors-Index-Outta-Bounds 
        DataC        105                       %% "index out of bounds"
        DataC        110                       
        DataC        100                       
        DataC        101                       
        DataC        120                       
        DataC        32                        
        DataC        111                       
        DataC        117                       
        DataC        116                       
        DataC        32                        
        DataC        111                       
        DataC        102                       
        DataC        32                        
        DataC        98                        
        DataC        111                       
        DataC        117                       
        DataC        110                       
        DataC        100                       
        DataC        115                       
        DataC        0                         
        Label        $$index-outta-bounds      
        PushD        $errors-Index-Outta-Bounds 
        Jump         $$general-runtime-error   
        DLabel       $errors-Range             
        DataC        114                       %% "range low > high"
        DataC        97                        
        DataC        110                       
        DataC        103                       
        DataC        101                       
        DataC        32                        
        DataC        108                       
        DataC        111                       
        DataC        119                       
        DataC        32                        
        DataC        62                        
        DataC        32                        
        DataC        104                       
        DataC        105                       
        DataC        103                       
        DataC        104                       
        DataC        0                         
        Label        $$range-bounds            
        PushD        $errors-Range             
        Jump         $$general-runtime-error   
        DLabel       $usable-memory-start      
        DLabel       $global-memory-block      
        DataZ        24                        
        Label        $$main                    
        Label        -mem-manager-initialize   
        DLabel       $heap-start-ptr           
        DataZ        4                         
        DLabel       $heap-after-ptr           
        DataZ        4                         
        DLabel       $heap-first-free          
        DataZ        4                         
        DLabel       $mmgr-newblock-block      
        DataZ        4                         
        DLabel       $mmgr-newblock-size       
        DataZ        4                         
        PushD        $heap-memory              
        Duplicate                              
        PushD        $heap-start-ptr           
        Exchange                               
        StoreI                                 
        PushD        $heap-after-ptr           
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushD        $heap-first-free          
        Exchange                               
        StoreI                                 
        Label        printTwoPlaces            
        PushD        $stack-pointer            
        LoadI                                  
        PushD        $frame-pointer            
        LoadI                                  
        Subtract                               
        JumpFalse    -beingCalled-4-join       
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushI        0                         
        Add                                    %% number
        LoadI                                  
        Nop                                    
        PushI        10                        
        Nop                                    
        Label        -compare-1-sub            
        Subtract                               
        JumpNeg      -compare-1-true           
        Jump         -compare-1-false          
        Label        -compare-1-true           
        PushI        1                         
        Jump         -compare-1-join           
        Label        -compare-1-false          
        PushI        0                         
        Jump         -compare-1-join           
        Label        -compare-1-join           
        JumpFalse    -compare-2-false          
        Jump         -compare-2-true           
        Label        -compare-2-true           
        PushD        $print-format-space       
        Printf                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushI        0                         
        Add                                    %% number
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        Jump         -compare-2-join           
        Label        -compare-2-false          
        PushD        $frame-pointer            
        LoadI                                  
        PushI        0                         
        Add                                    %% number
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        Jump         -compare-2-join           
        Label        -compare-2-join           
        PushD        $frame-pointer            
        LoadI                                  
        PushI        8                         
        Subtract                               
        LoadI                                  
        Return                                 
        Label        -beingCalled-4-join       
        Label        printFiboLine             
        PushD        $stack-pointer            
        LoadI                                  
        PushD        $frame-pointer            
        LoadI                                  
        Subtract                               
        JumpFalse    -beingCalled-8-join       
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        0                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        PushD        $frame-pointer            
        LoadI                                  
        PushI        4                         
        Add                                    %% i
        LoadI                                  
        StoreI                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        StoreI                                 
        PushD        $frame-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Add                                    
        StoreI                                 
        Call         printTwoPlaces            
        PushD        $frame-pointer            
        PushD        $frame-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        LoadI                                  
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        -8                        
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        0                         
        Subtract                               
        StoreI                                 
        PushI        0                         
        PushI        0                         
        Subtract                               
        JumpFalse    -funcStoreReturn-5-join   
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        PushI        0                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcStoreReturn-5-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcStoreReturn-5-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcStoreReturn-5-intLoad 
        Jump         -funcStoreReturn-5-floatLoad 
        Label        -funcStoreReturn-5-voidLoad 
        Pop                                    
        Pop                                    
        Jump         -funcStoreReturn-5-join   
        Label        -funcStoreReturn-5-charLoad 
        Pop                                    
        StoreC                                 
        Jump         -funcStoreReturn-5-join   
        Label        -funcStoreReturn-5-intLoad 
        Pop                                    
        StoreI                                 
        Jump         -funcStoreReturn-5-join   
        Label        -funcStoreReturn-5-floatLoad 
        Pop                                    
        StoreF                                 
        Jump         -funcStoreReturn-5-join   
        Label        -funcStoreReturn-5-join   
        PushD        $stack-pointer            
        LoadI                                  
        PushI        0                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcLoadReturn-6-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcLoadReturn-6-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcLoadReturn-6-intLoad 
        Jump         -funcLoadReturn-6-floatLoad 
        Label        -funcLoadReturn-6-voidLoad 
        Pop                                    
        Jump         -funcLoadReturn-6-join    
        Label        -funcLoadReturn-6-charLoad 
        Pop                                    
        LoadC                                  
        Jump         -funcLoadReturn-6-join    
        Label        -funcLoadReturn-6-intLoad 
        Pop                                    
        LoadI                                  
        Jump         -funcLoadReturn-6-join    
        Label        -funcLoadReturn-6-floatLoad 
        Pop                                    
        LoadF                                  
        Jump         -funcLoadReturn-6-join    
        Label        -funcLoadReturn-6-join    
        PushD        $print-format-space       
        Printf                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushI        0                         
        Add                                    %% fiboNumber
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushI        8                         
        Subtract                               
        LoadI                                  
        Return                                 
        Label        -beingCalled-8-join       
        Label        fibonacciNumber           
        PushD        $stack-pointer            
        LoadI                                  
        PushD        $frame-pointer            
        LoadI                                  
        Subtract                               
        JumpFalse    -beingCalled-20-join      
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushI        0                         
        Add                                    %% i
        LoadI                                  
        Nop                                    
        PushI        0                         
        Nop                                    
        Label        -compare-9-sub            
        Subtract                               
        JumpPos      -compare-9-false          
        Jump         -compare-9-true           
        Label        -compare-9-true           
        PushI        1                         
        Jump         -compare-9-join           
        Label        -compare-9-false          
        PushI        0                         
        Jump         -compare-9-join           
        Label        -compare-9-join           
        JumpFalse    -compare-11-false         
        Jump         -compare-11-true          
        Label        -compare-11-true          
        PushI        0                         
        PushD        $frame-pointer            
        LoadI                                  
        PushI        8                         
        Subtract                               
        LoadI                                  
        Return                                 
        Jump         -compare-11-join          
        Label        -compare-11-false         
        Jump         -compare-11-join          
        Label        -compare-11-join          
        PushD        $frame-pointer            
        LoadI                                  
        PushI        0                         
        Add                                    %% i
        LoadI                                  
        Nop                                    
        PushI        1                         
        Nop                                    
        Label        -compare-12-sub           
        Subtract                               
        JumpFalse    -compare-12-true          
        Jump         -compare-12-false         
        Label        -compare-12-true          
        PushI        1                         
        Jump         -compare-12-join          
        Label        -compare-12-false         
        PushI        0                         
        Jump         -compare-12-join          
        Label        -compare-12-join          
        JumpFalse    -compare-14-false         
        Jump         -compare-14-true          
        Label        -compare-14-true          
        PushI        1                         
        PushD        $frame-pointer            
        LoadI                                  
        PushI        8                         
        Subtract                               
        LoadI                                  
        Return                                 
        Jump         -compare-14-join          
        Label        -compare-14-false         
        Jump         -compare-14-join          
        Label        -compare-14-join          
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        PushD        $frame-pointer            
        LoadI                                  
        PushI        0                         
        Add                                    %% i
        LoadI                                  
        Nop                                    
        PushI        1                         
        Nop                                    
        Subtract                               
        StoreI                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        StoreI                                 
        PushD        $frame-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Add                                    
        StoreI                                 
        Call         fibonacciNumber           
        PushD        $frame-pointer            
        PushD        $frame-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        LoadI                                  
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        -8                        
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushI        4                         
        PushI        0                         
        Subtract                               
        JumpFalse    -funcStoreReturn-15-join  
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        PushI        4                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcStoreReturn-15-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcStoreReturn-15-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcStoreReturn-15-intLoad 
        Jump         -funcStoreReturn-15-floatLoad 
        Label        -funcStoreReturn-15-voidLoad 
        Pop                                    
        Pop                                    
        Jump         -funcStoreReturn-15-join  
        Label        -funcStoreReturn-15-charLoad 
        Pop                                    
        StoreC                                 
        Jump         -funcStoreReturn-15-join  
        Label        -funcStoreReturn-15-intLoad 
        Pop                                    
        StoreI                                 
        Jump         -funcStoreReturn-15-join  
        Label        -funcStoreReturn-15-floatLoad 
        Pop                                    
        StoreF                                 
        Jump         -funcStoreReturn-15-join  
        Label        -funcStoreReturn-15-join  
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcLoadReturn-16-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcLoadReturn-16-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcLoadReturn-16-intLoad 
        Jump         -funcLoadReturn-16-floatLoad 
        Label        -funcLoadReturn-16-voidLoad 
        Pop                                    
        Jump         -funcLoadReturn-16-join   
        Label        -funcLoadReturn-16-charLoad 
        Pop                                    
        LoadC                                  
        Jump         -funcLoadReturn-16-join   
        Label        -funcLoadReturn-16-intLoad 
        Pop                                    
        LoadI                                  
        Jump         -funcLoadReturn-16-join   
        Label        -funcLoadReturn-16-floatLoad 
        Pop                                    
        LoadF                                  
        Jump         -funcLoadReturn-16-join   
        Label        -funcLoadReturn-16-join   
        Nop                                    
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        PushD        $frame-pointer            
        LoadI                                  
        PushI        0                         
        Add                                    %% i
        LoadI                                  
        Nop                                    
        PushI        2                         
        Nop                                    
        Subtract                               
        StoreI                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        StoreI                                 
        PushD        $frame-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Add                                    
        StoreI                                 
        Call         fibonacciNumber           
        PushD        $frame-pointer            
        PushD        $frame-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        LoadI                                  
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        -8                        
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushI        4                         
        PushI        0                         
        Subtract                               
        JumpFalse    -funcStoreReturn-17-join  
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        PushI        4                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcStoreReturn-17-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcStoreReturn-17-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcStoreReturn-17-intLoad 
        Jump         -funcStoreReturn-17-floatLoad 
        Label        -funcStoreReturn-17-voidLoad 
        Pop                                    
        Pop                                    
        Jump         -funcStoreReturn-17-join  
        Label        -funcStoreReturn-17-charLoad 
        Pop                                    
        StoreC                                 
        Jump         -funcStoreReturn-17-join  
        Label        -funcStoreReturn-17-intLoad 
        Pop                                    
        StoreI                                 
        Jump         -funcStoreReturn-17-join  
        Label        -funcStoreReturn-17-floatLoad 
        Pop                                    
        StoreF                                 
        Jump         -funcStoreReturn-17-join  
        Label        -funcStoreReturn-17-join  
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcLoadReturn-18-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcLoadReturn-18-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcLoadReturn-18-intLoad 
        Jump         -funcLoadReturn-18-floatLoad 
        Label        -funcLoadReturn-18-voidLoad 
        Pop                                    
        Jump         -funcLoadReturn-18-join   
        Label        -funcLoadReturn-18-charLoad 
        Pop                                    
        LoadC                                  
        Jump         -funcLoadReturn-18-join   
        Label        -funcLoadReturn-18-intLoad 
        Pop                                    
        LoadI                                  
        Jump         -funcLoadReturn-18-join   
        Label        -funcLoadReturn-18-floatLoad 
        Pop                                    
        LoadF                                  
        Jump         -funcLoadReturn-18-join   
        Label        -funcLoadReturn-18-join   
        Nop                                    
        Add                                    
        PushD        $frame-pointer            
        LoadI                                  
        PushI        8                         
        Subtract                               
        LoadI                                  
        Return                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushI        8                         
        Subtract                               
        LoadI                                  
        Return                                 
        Label        -beingCalled-20-join      
        PushD        $global-memory-block      
        PushI        12                        
        Add                                    %% max
        PushI        200                       
        StoreI                                 
        PushD        $global-memory-block      
        PushI        16                        
        Add                                    %% n
        PushI        0                         
        StoreI                                 
        Label        -compare-27-while         
        PushD        $global-memory-block      
        PushI        16                        
        Add                                    %% n
        LoadI                                  
        Nop                                    
        PushI        25                        
        Nop                                    
        Label        -compare-21-sub           
        Subtract                               
        JumpNeg      -compare-21-true          
        Jump         -compare-21-false         
        Label        -compare-21-true          
        PushI        1                         
        Jump         -compare-21-join          
        Label        -compare-21-false         
        PushI        0                         
        Jump         -compare-21-join          
        Label        -compare-21-join          
        JumpFalse    -compare-27-join          
        Jump         -compare-27-true          
        Label        -compare-27-true          
        PushI        1                         
        PushD        $global-memory-block      
        PushI        20                        
        Add                                    %% thisFib
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        PushD        $global-memory-block      
        PushI        16                        
        Add                                    %% n
        LoadI                                  
        StoreI                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        StoreI                                 
        PushD        $frame-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Add                                    
        StoreI                                 
        Call         fibonacciNumber           
        PushD        $frame-pointer            
        PushD        $frame-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        LoadI                                  
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        -8                        
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushI        4                         
        PushI        0                         
        Subtract                               
        JumpFalse    -funcStoreReturn-22-join  
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        PushI        4                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcStoreReturn-22-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcStoreReturn-22-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcStoreReturn-22-intLoad 
        Jump         -funcStoreReturn-22-floatLoad 
        Label        -funcStoreReturn-22-voidLoad 
        Pop                                    
        Pop                                    
        Jump         -funcStoreReturn-22-join  
        Label        -funcStoreReturn-22-charLoad 
        Pop                                    
        StoreC                                 
        Jump         -funcStoreReturn-22-join  
        Label        -funcStoreReturn-22-intLoad 
        Pop                                    
        StoreI                                 
        Jump         -funcStoreReturn-22-join  
        Label        -funcStoreReturn-22-floatLoad 
        Pop                                    
        StoreF                                 
        Jump         -funcStoreReturn-22-join  
        Label        -funcStoreReturn-22-join  
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcLoadReturn-23-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcLoadReturn-23-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcLoadReturn-23-intLoad 
        Jump         -funcLoadReturn-23-floatLoad 
        Label        -funcLoadReturn-23-voidLoad 
        Pop                                    
        Jump         -funcLoadReturn-23-join   
        Label        -funcLoadReturn-23-charLoad 
        Pop                                    
        LoadC                                  
        Jump         -funcLoadReturn-23-join   
        Label        -funcLoadReturn-23-intLoad 
        Pop                                    
        LoadI                                  
        Jump         -funcLoadReturn-23-join   
        Label        -funcLoadReturn-23-floatLoad 
        Pop                                    
        LoadF                                  
        Jump         -funcLoadReturn-23-join   
        Label        -funcLoadReturn-23-join   
        StoreI                                 
        Duplicate                              
        JumpNeg      -LoopBlock-26-continue    
        Duplicate                              
        JumpFalse    -LoopBlock-26-continue    
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        PushD        $global-memory-block      
        PushI        16                        
        Add                                    %% n
        LoadI                                  
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        PushD        $global-memory-block      
        PushI        20                        
        Add                                    %% thisFib
        LoadI                                  
        StoreI                                 
        PushD        $frame-pointer            
        LoadI                                  
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        StoreI                                 
        PushD        $frame-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        4                         
        Add                                    
        StoreI                                 
        Call         printFiboLine             
        PushD        $frame-pointer            
        PushD        $frame-pointer            
        LoadI                                  
        PushI        4                         
        Subtract                               
        LoadI                                  
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        -8                        
        Subtract                               
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $stack-pointer            
        LoadI                                  
        PushI        0                         
        Subtract                               
        StoreI                                 
        PushI        0                         
        PushI        0                         
        Subtract                               
        JumpFalse    -funcStoreReturn-24-join  
        PushD        $stack-pointer            
        LoadI                                  
        Exchange                               
        PushI        0                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcStoreReturn-24-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcStoreReturn-24-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcStoreReturn-24-intLoad 
        Jump         -funcStoreReturn-24-floatLoad 
        Label        -funcStoreReturn-24-voidLoad 
        Pop                                    
        Pop                                    
        Jump         -funcStoreReturn-24-join  
        Label        -funcStoreReturn-24-charLoad 
        Pop                                    
        StoreC                                 
        Jump         -funcStoreReturn-24-join  
        Label        -funcStoreReturn-24-intLoad 
        Pop                                    
        StoreI                                 
        Jump         -funcStoreReturn-24-join  
        Label        -funcStoreReturn-24-floatLoad 
        Pop                                    
        StoreF                                 
        Jump         -funcStoreReturn-24-join  
        Label        -funcStoreReturn-24-join  
        PushD        $stack-pointer            
        LoadI                                  
        PushI        0                         
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpNeg      -funcLoadReturn-25-voidLoad 
        Duplicate                              
        PushI        1                         
        Subtract                               
        JumpFalse    -funcLoadReturn-25-charLoad 
        Duplicate                              
        PushI        4                         
        Subtract                               
        JumpFalse    -funcLoadReturn-25-intLoad 
        Jump         -funcLoadReturn-25-floatLoad 
        Label        -funcLoadReturn-25-voidLoad 
        Pop                                    
        Jump         -funcLoadReturn-25-join   
        Label        -funcLoadReturn-25-charLoad 
        Pop                                    
        LoadC                                  
        Jump         -funcLoadReturn-25-join   
        Label        -funcLoadReturn-25-intLoad 
        Pop                                    
        LoadI                                  
        Jump         -funcLoadReturn-25-join   
        Label        -funcLoadReturn-25-floatLoad 
        Pop                                    
        LoadF                                  
        Jump         -funcLoadReturn-25-join   
        Label        -funcLoadReturn-25-join   
        Duplicate                              
        JumpNeg      -LoopBlock-26-continue    
        Duplicate                              
        JumpFalse    -LoopBlock-26-continue    
        PushD        $global-memory-block      
        PushI        16                        
        Add                                    %% n
        PushD        $global-memory-block      
        PushI        16                        
        Add                                    %% n
        LoadI                                  
        Nop                                    
        PushI        1                         
        Nop                                    
        Add                                    
        StoreI                                 
        Duplicate                              
        JumpNeg      -LoopBlock-26-continue    
        Duplicate                              
        JumpFalse    -LoopBlock-26-continue    
        Jump         -LoopBlock-26-join        
        Label        -LoopBlock-26-continue    
        Exchange                               
        Pop                                    
        Exchange                               
        Pop                                    
        Jump         -LoopBlock-26-join        
        Label        -LoopBlock-26-join        
        JumpFalse    -compare-27-join          
        Jump         -compare-27-while         
        Label        -compare-27-join          
        DLabel       -stringConstant-28-       
        DataI        3                         
        DataI        9                         
        DataI        9                         
        DataC        97                        %% "all done."
        DataC        108                       
        DataC        108                       
        DataC        32                        
        DataC        100                       
        DataC        111                       
        DataC        110                       
        DataC        101                       
        DataC        46                        
        DataC        0                         
        PushD        -stringConstant-28-       
        PushI        12                        
        Add                                    
        PushD        $print-format-string      
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        Halt                                   
        Label        -mem-manager-make-tags    
        DLabel       $mmgr-tags-size           
        DataZ        4                         
        DLabel       $mmgr-tags-start          
        DataZ        4                         
        DLabel       $mmgr-tags-available      
        DataZ        4                         
        DLabel       $mmgr-tags-nextptr        
        DataZ        4                         
        DLabel       $mmgr-tags-prevptr        
        DataZ        4                         
        DLabel       $mmgr-tags-return         
        DataZ        4                         
        PushD        $mmgr-tags-return         
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-size           
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-start          
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-available      
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-nextptr        
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-prevptr        
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-prevptr        
        LoadI                                  
        PushD        $mmgr-tags-size           
        LoadI                                  
        PushD        $mmgr-tags-available      
        LoadI                                  
        PushD        $mmgr-tags-start          
        LoadI                                  
        Call         -mem-manager-one-tag      
        PushD        $mmgr-tags-nextptr        
        LoadI                                  
        PushD        $mmgr-tags-size           
        LoadI                                  
        PushD        $mmgr-tags-available      
        LoadI                                  
        PushD        $mmgr-tags-start          
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        Call         -mem-manager-one-tag      
        PushD        $mmgr-tags-return         
        LoadI                                  
        Return                                 
        Label        -mem-manager-one-tag      
        DLabel       $mmgr-onetag-return       
        DataZ        4                         
        DLabel       $mmgr-onetag-location     
        DataZ        4                         
        DLabel       $mmgr-onetag-available    
        DataZ        4                         
        DLabel       $mmgr-onetag-size         
        DataZ        4                         
        DLabel       $mmgr-onetag-pointer      
        DataZ        4                         
        PushD        $mmgr-onetag-return       
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-location     
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-available    
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-size         
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-location     
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-size         
        LoadI                                  
        PushD        $mmgr-onetag-location     
        LoadI                                  
        PushI        4                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-available    
        LoadI                                  
        PushD        $mmgr-onetag-location     
        LoadI                                  
        PushI        8                         
        Add                                    
        Exchange                               
        StoreC                                 
        PushD        $mmgr-onetag-return       
        LoadI                                  
        Return                                 
        Label        -mem-manager-allocate     
        DLabel       $mmgr-alloc-return        
        DataZ        4                         
        DLabel       $mmgr-alloc-size          
        DataZ        4                         
        DLabel       $mmgr-alloc-current-block 
        DataZ        4                         
        DLabel       $mmgr-alloc-remainder-block 
        DataZ        4                         
        DLabel       $mmgr-alloc-remainder-size 
        DataZ        4                         
        PushD        $mmgr-alloc-return        
        Exchange                               
        StoreI                                 
        PushI        18                        
        Add                                    
        PushD        $mmgr-alloc-size          
        Exchange                               
        StoreI                                 
        PushD        $heap-first-free          
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        Exchange                               
        StoreI                                 
        Label        -mmgr-alloc-process-current 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        JumpFalse    -mmgr-alloc-no-block-works 
        Label        -mmgr-alloc-test-block    
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Subtract                               
        PushI        1                         
        Add                                    
        JumpPos      -mmgr-alloc-found-block   
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        Exchange                               
        StoreI                                 
        Jump         -mmgr-alloc-process-current 
        Label        -mmgr-alloc-found-block   
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        Call         -mem-manager-remove-block 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Subtract                               
        PushI        26                        
        Subtract                               
        JumpNeg      -mmgr-alloc-return-userblock 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Add                                    
        PushD        $mmgr-alloc-remainder-block 
        Exchange                               
        StoreI                                 
        PushD        $mmgr-alloc-size          
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        Exchange                               
        Subtract                               
        PushD        $mmgr-alloc-remainder-size 
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushI        0                         
        PushI        0                         
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Call         -mem-manager-make-tags    
        PushI        0                         
        PushI        0                         
        PushI        1                         
        PushD        $mmgr-alloc-remainder-block 
        LoadI                                  
        PushD        $mmgr-alloc-remainder-size 
        LoadI                                  
        Call         -mem-manager-make-tags    
        PushD        $mmgr-alloc-remainder-block 
        LoadI                                  
        PushI        9                         
        Add                                    
        Call         -mem-manager-deallocate   
        Jump         -mmgr-alloc-return-userblock 
        Label        -mmgr-alloc-no-block-works 
        PushD        $mmgr-alloc-size          
        LoadI                                  
        PushD        $mmgr-newblock-size       
        Exchange                               
        StoreI                                 
        PushD        $heap-after-ptr           
        LoadI                                  
        PushD        $mmgr-newblock-block      
        Exchange                               
        StoreI                                 
        PushD        $mmgr-newblock-size       
        LoadI                                  
        PushD        $heap-after-ptr           
        LoadI                                  
        Add                                    
        PushD        $heap-after-ptr           
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushI        0                         
        PushI        0                         
        PushD        $mmgr-newblock-block      
        LoadI                                  
        PushD        $mmgr-newblock-size       
        LoadI                                  
        Call         -mem-manager-make-tags    
        PushD        $mmgr-newblock-block      
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        Exchange                               
        StoreI                                 
        Label        -mmgr-alloc-return-userblock 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        9                         
        Add                                    
        PushD        $mmgr-alloc-return        
        LoadI                                  
        Return                                 
        Label        -mem-manager-deallocate   
        DLabel       $mmgr-dealloc-return      
        DataZ        4                         
        DLabel       $mmgr-dealloc-block       
        DataZ        4                         
        PushD        $mmgr-dealloc-return      
        Exchange                               
        StoreI                                 
        PushI        9                         
        Subtract                               
        PushD        $mmgr-dealloc-block       
        Exchange                               
        StoreI                                 
        PushD        $heap-first-free          
        LoadI                                  
        JumpFalse    -mmgr-bypass-firstFree    
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushD        $heap-first-free          
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        Label        -mmgr-bypass-firstFree    
        PushI        0                         
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $heap-first-free          
        LoadI                                  
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushI        1                         
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushI        8                         
        Add                                    
        Exchange                               
        StoreC                                 
        PushI        1                         
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        8                         
        Add                                    
        Exchange                               
        StoreC                                 
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushD        $heap-first-free          
        Exchange                               
        StoreI                                 
        PushD        $mmgr-dealloc-return      
        LoadI                                  
        Return                                 
        Label        -mem-manager-remove-block 
        DLabel       $mmgr-remove-return       
        DataZ        4                         
        DLabel       $mmgr-remove-block        
        DataZ        4                         
        DLabel       $mmgr-remove-prev         
        DataZ        4                         
        DLabel       $mmgr-remove-next         
        DataZ        4                         
        PushD        $mmgr-remove-return       
        Exchange                               
        StoreI                                 
        PushD        $mmgr-remove-block        
        Exchange                               
        StoreI                                 
        PushD        $mmgr-remove-block        
        LoadI                                  
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-remove-prev         
        Exchange                               
        StoreI                                 
        PushD        $mmgr-remove-block        
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-remove-next         
        Exchange                               
        StoreI                                 
        Label        -mmgr-remove-process-prev 
        PushD        $mmgr-remove-prev         
        LoadI                                  
        JumpFalse    -mmgr-remove-no-prev      
        PushD        $mmgr-remove-next         
        LoadI                                  
        PushD        $mmgr-remove-prev         
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        Jump         -mmgr-remove-process-next 
        Label        -mmgr-remove-no-prev      
        PushD        $mmgr-remove-next         
        LoadI                                  
        PushD        $heap-first-free          
        Exchange                               
        StoreI                                 
        Label        -mmgr-remove-process-next 
        PushD        $mmgr-remove-next         
        LoadI                                  
        JumpFalse    -mmgr-remove-done         
        PushD        $mmgr-remove-prev         
        LoadI                                  
        PushD        $mmgr-remove-next         
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        Label        -mmgr-remove-done         
        PushD        $mmgr-remove-return       
        LoadI                                  
        Return                                 
        DLabel       $heap-memory              
