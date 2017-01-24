package com.jindata.apiserver.service.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.gson.Gson;
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.service.dto.Role;
import com.jindata.apiserver.service.dto.Roletarget;
import com.jindata.apiserver.service.dto.User;

import redis.clients.jedis.Jedis;
/**
 * targetURI : 대상 URI
 * targetMethod : 대상 메소드(이것으로 읽기 쓰기가 구분되지 않는다)
 * permission : 퍼미션 값 (0:권한없음,1:프로세스 실행,2:읽기,4:쓰기,8:삭제,16:양도가능, 권한은 이 숫자들의 합으로 나타낸다.)
 * @author SGcom
 *
 */
public class ApiServerRealm extends AuthorizingRealm {
    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private SecurityManager secMgr;
    @Autowired
    @Qualifier("sessionRetentionTime")
    private int sessionRetentionTime;
    private JedisHelper helper = JedisHelper.getInstance();


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        long userno = (long)principals.fromRealm(getName()).iterator().next();
        Map<String,Long> param = new HashMap<>();
        param.put("userno", userno);
        PermissionResolver pr = getPermissionResolver();
        
        User user = sqlSession.selectOne("users.getInfoByuserno",param);
        if(user != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            for(Role role:user.getRoles()) {
                info.addRole(role.getDescription());
                for(Roletarget target:role.getTargets()) {
                    info.addObjectPermission(pr.resolvePermission(target.getTargetURI()+":"+target.getPermission()+":"+target.getTargetMethod()));
                }
            }
            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upt = (UsernamePasswordToken) token;
        Map<String,String> param = new HashMap<>();
        param.put("email", upt.getUsername());
        param.put("password", new String(upt.getPassword()));
        long issueDate = System.currentTimeMillis();
        
        User user = sqlSession.selectOne("users.userLogin", param);
        
        Jedis jedis = helper.getConnection();
        
        if(user != null) {
            if(!jedis.exists(upt.getHost())){
                Map<String,Object> expinfo = new HashMap<>();
                expinfo.put("issueDate", issueDate);
                expinfo.put("expireDate", issueDate + sessionRetentionTime);
                expinfo.put("userInfo", user.toString());
                
                jedis.setex(upt.getHost(), sessionRetentionTime, new Gson().toJson(expinfo));
            }
            
            Map<String,String> updateParam = new HashMap<>();
            updateParam.put("userno", Long.toString(user.getUserno()));
            sqlSession.update("users.postLogin",updateParam);
            
            return new SimpleAuthenticationInfo(user.getUserno(),user.getPassword(),getName());
        }
        return null;
    }
}
