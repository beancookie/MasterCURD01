package org.linlinjava.litemall.admin.web;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallIntegral;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallIntegralService;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.util.IntegralConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallSystemConfigService systemConfigService;

    @Autowired
    private LitemallIntegralService integralService;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username, String mobile,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallUser> userList = userService.querySelective(username, mobile, page, limit, sort, order);
        long total = PageInfo.of(userList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", userList);

        return ResponseUtil.ok(data);
    }

    @PutMapping("/integral")
    public Object add(@RequestParam("id") int userId, @RequestParam("integral") long integral) {
        LitemallUser litemallUser = userService.findById(userId);
        /**
         * 把积分变化量保存到积分变化表
         */
        LitemallIntegral integralChange = new LitemallIntegral();
        integralChange.setUserId(userId);
        integralChange.setType(IntegralConstant.ADMIN);
        integralChange.setIntegral((int) (integral - litemallUser.getIntegral()));
        integralService.add(integralChange);

        /**
         * 更新用户积分
         */
        litemallUser.setIntegral(integral);
        int result = userService.updateById(litemallUser);
        if (result > 0) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.fail();
        }
    }

    @PutMapping("/integralConfig")
    public Object updateIntegralConfig(@RequestParam("signinType") Integer type, @RequestParam(value = "signinIntegral") Integer signIntegral) {
        Map<String, String> signinConfig = new HashMap<>();
        signinConfig.put(IntegralConstant.SIGNIN_TYPE_KEY, type.toString());
        signinConfig.put(IntegralConstant.SIGNIN_INTEGRAL_KEY, signIntegral.toString());
        systemConfigService.updateConfig(signinConfig);
        return ResponseUtil.ok();
    }

    @GetMapping("/integralAndType")
    public Object getSignalAndType() {
        Map<String, String> signinConfig = systemConfigService.listSignin();
        Map<String, Object> data = new HashMap<>(signinConfig.size());
        data.put("signinType", signinConfig.get(IntegralConstant.SIGNIN_TYPE_KEY));
        data.put("signinIntegral", signinConfig.get(IntegralConstant.SIGNIN_INTEGRAL_KEY));
        return ResponseUtil.ok(data);
    }

}
