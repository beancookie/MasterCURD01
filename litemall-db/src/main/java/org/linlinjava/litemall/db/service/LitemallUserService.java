package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallSignMapper;
import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LitemallUserService {
    @Resource
    private LitemallUserMapper userMapper;

//    @Resource
//    private LitemallIntegralMapper integralMapper;

    @Autowired
    private LitemallSignMapper litemallSignMapper;

    public LitemallUser findById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public UserVo findUserVoById(Integer userId) {
        LitemallUser user = findById(userId);
        UserVo userVo = new UserVo();
        userVo.setNickname(user.getNickname());
        userVo.setAvatar(user.getAvatar());
        return userVo;
    }

    public LitemallUser queryByOid(String openId) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public void add(LitemallUser user) {
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);
    }

    public int updateById(LitemallUser user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public List<LitemallUser> querySelective(String username, String mobile, Integer page, Integer size, String sort, String order) {
        LitemallUserExample example = new LitemallUserExample();
        LitemallUserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(mobile)) {
            criteria.andMobileEqualTo(mobile);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return userMapper.selectByExample(example);
    }

    public int count() {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andDeletedEqualTo(false);

        return (int) userMapper.countByExample(example);
    }

    public List<LitemallUser> queryByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public boolean checkByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.countByExample(example) != 0;
    }

    public List<LitemallUser> queryByMobile(String mobile) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public List<LitemallUser> queryByOpenid(String openid) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        userMapper.logicalDeleteByPrimaryKey(id);
    }


//    public List<UserIntegral> selectAllUserAndIntegral(String username, String mobile, Integer page, Integer size, String sort, String order){
//       List<UserIntegral> list=new ArrayList<>();
//       if(Strings.isNotEmpty(username)){
//
//       }
//        List<LitemallUser> litemallUsers = querySelective(username, mobile, page, size, sort, order);
//        for(int i=0;i<litemallUsers.size();i++){
//            UserIntegral userIntegral=new UserIntegral();
//            userIntegral.setId(litemallUsers.get(i).getId());
//            //userIntegral.setUsername(litemallUsers.get(i).getUsername());
//            userIntegral.setMobile(litemallUsers.get(i).getMobile());
//            userIntegral.setGender(litemallUsers.get(i).getGender());
//            userIntegral.setBirthday(litemallUsers.get(i).getBirthday());
//            userIntegral.setUserLevel(litemallUsers.get(i).getUserLevel());
//            userIntegral.setStatus(litemallUsers.get(i).getStatus());
//            LitemallIntegral litemallIntegral = integralMapper.selectByPrimaryKey(litemallUsers.get(i).getId());
//            userIntegral.setLitemallIntegral(litemallIntegral);
//            list.add(userIntegral);
//        }
//        return list;
//    }


    public int updateIntegralById(Integer id){
        //type 0表示随机  1表示订制
        LitemallUser litemallUser1 = userMapper.selectByPrimaryKey(id);
        LitemallSign litemallSign = litemallSignMapper.selectByPrimaryKey(2);
        LitemallUser litemallUser=new LitemallUser();
        if(litemallSign.getType()==0){
            int sign=litemallUser1.getIntegral()+(int)(Math.random()*3+1);
            litemallUser.setIntegral(sign);
            litemallUser.setId(id);
            int i = userMapper.updateByPrimaryKeySelective(litemallUser);
            return i;
        }
        if(litemallSign.getType()==1){
            int sign=litemallSign.getSignIntegral()+litemallUser1.getIntegral();
            litemallUser.setIntegral(sign);
            litemallUser.setId(id);
            int i=userMapper.updateByPrimaryKeySelective(litemallUser);
            return i;
        }
        return -1;
    }


    public int updateIntegral(Integer type,Integer siginIntegral ){
        //type 0表示随机  1表示订制
        LitemallSign litemallSign=new LitemallSign();
        litemallSign.setId(2);
        if(type==1){
            litemallSign.setSignIntegral(siginIntegral);
            litemallSign.setType(type);
            return litemallSignMapper.updateByPrimaryKeySelective(litemallSign);
        }
        if(type==0){
            litemallSign.setType(type);
            return litemallSignMapper.updateByPrimaryKeySelective(litemallSign);
        }
           return -1;
    }

    public LitemallSign getTypeAndSign(){
        return litemallSignMapper.selectByPrimaryKey(2);
    }

}
