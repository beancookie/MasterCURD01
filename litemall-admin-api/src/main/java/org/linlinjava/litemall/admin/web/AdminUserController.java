package org.linlinjava.litemall.admin.web;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.util.bcrypt.BCryptPasswordEncoder;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallSign;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.util.IntegralConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.*;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private LitemallUserService userService;

    @Autowired
    private LitemallSystemConfigService systemConfigService;

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
    public Object add(@RequestParam("id") int id, @RequestParam("integral") long integral) {
        LitemallUser litemallUser = new LitemallUser();
        litemallUser.setId(id);
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
