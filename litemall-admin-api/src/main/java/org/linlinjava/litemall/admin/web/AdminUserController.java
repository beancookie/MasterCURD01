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
import org.linlinjava.litemall.db.service.LitemallUserService;
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

    @GetMapping("/addByIntegral")
    public Object add(@RequestParam("id") int id, @RequestParam("integral") int integral) {
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

    @GetMapping("/SignIntegral")
    public Object SignIntegral(@RequestParam(value = "id", required = false, defaultValue = "-1") Integer id) {
        int result = userService.updateIntegralById(id);
        if (result > 0) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.fail(-1, "签到失败");
        }
    }

    @GetMapping("updateSignIntegral")
    public Object updateSignIntegral(@RequestParam("type") Integer type, @RequestParam(value = "signIntegral", required = false) Integer signIntegral) {
        int result = userService.updateIntegral(type, signIntegral);
        if (result > 0) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.fail();
        }
    }

    @GetMapping("getSignalAndType")
    public Object getSignalAndType() {
        return userService.getTypeAndSign();
    }

}
