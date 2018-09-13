>What the project does
This project inroduce Assert created for wrapping regular TestNg Assert,
 but with abilty to perform verification after some conditional wait,
 without waiting by test case. 
 For example, if you receiving email with some delay, you can init asert of 
 email and close test case without waiting, and run next one. In other thred, 
 after email is recieved, assert would be executed and test result updated.
 You need to provide supplier that should provide with some result ower time
 and consumer, that should perform some regular asserts over supplied data.
 
Something like         
AsyncAssert.aAssert("Some description",
		() -> {
                    Thread.sleep(5000);
                    return "response data";
                }, data -> Assert.assertEquals(data, "response data"));
