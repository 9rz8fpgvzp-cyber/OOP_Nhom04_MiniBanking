package vn.edu.ptit.oop.nhom04.service;

import java.time.*;
import java.util.*;
import vn.edu.ptit.oop.nhom04.model.PinCredential;
import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.exception.*;

public class PinServiceTest {
    static int checks;
    static final Instant START=Instant.parse("2026-10-27T02:00:00Z");
    static class MutableClock extends Clock {
        Instant time=START;
        public ZoneId getZone(){return ZoneOffset.UTC;}
        public Clock withZone(ZoneId zone){return this;}
        public Instant instant(){return time;}
    }
    static void check(boolean b,String name){checks++;if(!b)throw new AssertionError(name);}
    static void rejects(Class<? extends Throwable> type,Runnable r){checks++;try{r.run();}catch(Throwable t){if(type.isInstance(t))return;throw new AssertionError(t);}throw new AssertionError("Expected "+type.getSimpleName());}
    static char[] pin(String text){return text.toCharArray();}
    static void wiped(char[] chars){for(char ch:chars)check(ch=='\0',"PIN buffer not wiped");}
    static void threeWrong(PinService p,String id){check(!p.authenticate(id,pin("999999")),"wrong1 false");check(!p.authenticate(id,pin("999999")),"wrong2 false");rejects(AccountLockedException.class,()->p.authenticate(id,pin("999999")));}
    public static void main(String[] args)throws Exception{
        MutableClock clock=new MutableClock();PinService p=new PinService(clock);
        char[] original=pin("001234");p.registerPin("KH001",original);wiped(original);
        p.registerPin("KH002",pin("234567"));check(p.getAuthenticatedCustomerId().isEmpty(),"no auto-login");
        check(p.getRemainingAttempts("KH001")==3,"3 initial attempts");check(p.getRemainingLockSeconds("KH001")==0&&!p.isLocked("KH001"),"initially unlocked");
        for(String invalid:Arrays.asList(null,"","   ","UNKNOWN"))check(!p.isAuthenticated(invalid),"unknown cannot be authenticated");
        rejects(AccountNotFoundException.class,()->p.authenticate("UNKNOWN",pin("001234")));
        rejects(AccountNotFoundException.class,()->p.getRemainingAttempts("UNKNOWN"));rejects(AccountNotFoundException.class,()->p.isLocked("UNKNOWN"));
        rejects(AccountNotFoundException.class,()->p.getRemainingLockSeconds("UNKNOWN"));rejects(AccountNotFoundException.class,()->p.requireAuthenticated("UNKNOWN"));
        rejects(AuthenticationRequiredException.class,()->p.requireAuthenticated("KH001"));
        check(!p.authenticate("KH001",null),"null PIN counts wrong");check(p.getRemainingAttempts("KH001")==2,"null consumes one");
        char[] malformed=pin("abc");check(!p.authenticate("KH001",malformed),"malformed counts wrong");wiped(malformed);check(p.getRemainingAttempts("KH001")==1,"two failures");
        char[] correct=pin("001234");check(p.authenticate("KH001",correct),"leading-zero correct PIN");wiped(correct);
        check(p.getRemainingAttempts("KH001")==3,"success resets failures");check(p.getAuthenticatedCustomerId().orElseThrow().equals("KH001"),"current customer");p.requireAuthenticated("KH001");
        rejects(AuthenticationRequiredException.class,()->p.requireAuthenticated("KH002"));
        check(!p.authenticate("KH002",pin("999999")),"wrong other user");check(p.getAuthenticatedCustomerId().isEmpty(),"attempt clears old session");
        p.logout();check(p.getRemainingAttempts("KH002")==2,"logout preserves failure count");
        check(p.authenticate("KH002",pin("234567")),"B success");check(p.getRemainingAttempts("KH002")==3,"B reset");
        threeWrong(p,"KH001");check(p.isLocked("KH001"),"third locks");check(p.getRemainingAttempts("KH001")==0,"no attempts locked");
        check(p.getRemainingLockSeconds("KH001")==30,"30 seconds locked");check(p.getAuthenticatedCustomerId().isEmpty(),"lock leaves no session");
        rejects(AccountLockedException.class,()->p.requireAuthenticated("KH001"));
        rejects(AccountLockedException.class,()->p.authenticate("KH001",pin("001234")));
        char[] duplicate=pin("001234");rejects(IllegalArgumentException.class,()->p.registerPin("KH001",duplicate));wiped(duplicate);check(p.isLocked("KH001"),"duplicate cannot clear lock");
        clock.time=START.plusSeconds(1);rejects(AccountLockedException.class,()->p.authenticate("KH001",pin("999999")));check(p.getRemainingLockSeconds("KH001")==29,"attempt does not extend lock");
        check(p.authenticate("KH002",pin("234567")),"B can login while A locked");check(!p.isAuthenticated("KH001")&&p.isAuthenticated("KH002"),"A lock independent B");
        p.logout();check(p.isLocked("KH001"),"logout preserves lock");
        clock.time=START.plusMillis(29_999);check(p.isLocked("KH001"),"29.999s locked");check(p.getRemainingLockSeconds("KH001")==1,"round fractions up");rejects(AccountLockedException.class,()->p.authenticate("KH001",pin("001234")));
        clock.time=START.plusSeconds(30);check(!p.isLocked("KH001"),"30.000s unlocks");check(p.getRemainingLockSeconds("KH001")==0,"no remaining lock");check(p.getRemainingAttempts("KH001")==3,"expiry resets attempts");
        check(!p.isAuthenticated("KH001")&&p.getAuthenticatedCustomerId().isEmpty(),"expiry does not auto login");check(p.authenticate("KH001",pin("001234")),"login after expiry");
        p.logout();check(!p.isAuthenticated("KH001"),"logout clears session");
        for(String bad:List.of("12345","1234567","12a456","１２３４５６"))check(!PinCredential.isValidPin(pin(bad)),"ASCII 6-digit required");check(!PinCredential.isValidPin(null),"null invalid");
        for(String bad:List.of("12345","1234567","12a456"))rejects(IllegalArgumentException.class,()->p.registerPin("BAD",pin(bad)));
        check(p.authenticate(" KH001 ",pin("001234")),"authenticate trims user");
        check(p.isAuthenticated(" KH001 "),"isAuthenticated trims user");
        p.requireAuthenticated(" KH001 ");
        check(p.getRemainingAttempts(" KH001 ")==3,"remaining attempts trims user");
        PinService padded=new PinService(clock);padded.registerPin(" KH003 ",pin("456789"));
        check(padded.authenticate("KH003",pin("456789")),"registration normalizes user");
        rejects(IllegalArgumentException.class,()->padded.registerPin("KH003",pin("456789")));
        MutableClock credentialClock=new MutableClock();
        PinCredential credential=new PinCredential(pin("001234"),credentialClock);
        for(int i=0;i<3;i++)credential.recordFailedAttempt();
        check(credential.isLocked(),"credential holds lock");
        check(!credential.matches(pin("001234")),"credential matches rejects locked PIN");
        credential.resetFailedAttempts();check(credential.getRemainingAttempts()==0,"reset cannot bypass lock");
        credentialClock.time=START.plusMillis(29_999);
        check(credential.getRemainingLockSeconds()==1&&!credential.matches(pin("001234")),"credential expiry boundary");
        credentialClock.time=START.plusSeconds(30);
        check(credential.matches(pin("001234"))&&credential.getRemainingAttempts()==3,"credential unlocks on deadline");
        System.out.println("PinServiceTest: PASS ("+checks+" checks)");
    }
}
