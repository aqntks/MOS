package com.moskhu.web;

import com.moskhu.s3.S3Service;
import com.moskhu.service.posts.*;
import com.moskhu.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Controller
public class MosController {

    private final MenuService menuService;
    private final OrderMenuService orderMenuService;
    private final StatusService statusService;
    private final S3Service s3Service;

    @GetMapping("/") //시작 화면
    public String start(Model model) {
        if (statusService.existsById(1L)) {//status 데이터 존재 여부
            if (statusService.findById(1L).isOn_off())
                return "start";
            else
                return "preparing";
        } else
            return "preparing";
    }

    @GetMapping("/order") //주문 화면
    public String order(Model model) {
        List<MenuListResponseDto> menu = menuService.findAllDesc();

        List<MenuListResponseDto> type1 = new ArrayList<>();
        List<MenuListResponseDto> type2 = new ArrayList<>();
        List<MenuListResponseDto> type3 = new ArrayList<>();
        List<MenuListResponseDto> type4 = new ArrayList<>();
        List<MenuListResponseDto> type5 = new ArrayList<>();

        for(MenuListResponseDto m : menu) {
            switch(m.getMenuType())
            {
                case 1:
                    type1.add(m);
                    break;
                case 2:
                    type2.add(m);
                    break;
                case 3:
                    type3.add(m);
                    break;
                case 4:
                    type4.add(m);
                    break;
                case 5:
                    type5.add(m);
                    break;
                default:
            }
        }

        model.addAttribute("menu", menu);
        model.addAttribute("type1", type1);
        model.addAttribute("type2", type2);
        model.addAttribute("type3", type3);
        model.addAttribute("type4", type4);
        model.addAttribute("type5", type5);
        return "order";
    }

    @GetMapping("/cart") //장바구니 화면
    public String cart(Model model) {
        return "cart";
    }

    @GetMapping("/payment") //결제 화면
    public String payment(Model model) {
        return "payment";
    }

    @GetMapping("/payment_success") //결제 성공
    public String payment_success(Model model) {
        return "payment_success";
    }

    @GetMapping("/result/{id}") //결과 화면
    public String result(Model model, @PathVariable Long id) throws ParseException {
        OrderMenuResponseDto om = orderMenuService.findById(id);
        List<JSONObject> list = new ArrayList<>();
        JSONParser parser = new JSONParser();

        for(String s : om.getMenus())
            list.add((JSONObject) parser.parse(s));

        model.addAttribute("menus", list);
        model.addAttribute("total", om.getTotal());
        model.addAttribute("id", id);
        model.addAttribute("status", "조리중");
        return "result";
    }

    @GetMapping("/finish")
    public String finish(Model model){
        return "finish";
    }

    ////////////////////////////////////////////////////////////////////////////// 판매자

    @GetMapping("/seller_start") //판매자 시작 화면
    public String seller_start(Model model){
            return "seller_start";
    }

    @GetMapping("/sales") //판매 화면
    public String sales(Model model) throws ParseException {
        List<OrderMenuListResponseDto> list = orderMenuService.findAllDesc();
        List<OrderMenuListJsonDto> jList = new ArrayList<>();
        for(OrderMenuListResponseDto o : list)
            jList.add(new OrderMenuListJsonDto(o));

        model.addAttribute("orderMenu", jList);

        //판매 상태 출력
        String str = "";
        if(statusService.existsById(1L)){//status 데이터 존재 여부
            if(statusService.findById(1L).isOn_off()) {
                str += "판매중...";
                model.addAttribute("status", str);
            }
            else {
                str += "준비중...";
                model.addAttribute("status", str);
            }
        }
        else {
            str += "준비중...";
            model.addAttribute("status", str);
        }

        return "sales";
    }

    @GetMapping("/menu_management") //메뉴 관리 화면
    public String menu_management(Model model) {
        List<MenuListResponseDto> menu = menuService.findAllDesc();

        List<MenuListResponseDto> type1 = new ArrayList<>();
        List<MenuListResponseDto> type2 = new ArrayList<>();
        List<MenuListResponseDto> type3 = new ArrayList<>();
        List<MenuListResponseDto> type4 = new ArrayList<>();
        List<MenuListResponseDto> type5 = new ArrayList<>();

        for(MenuListResponseDto m : menu) {
            switch(m.getMenuType())
            {
                case 1:
                    type1.add(m);
                    break;
                case 2:
                    type2.add(m);
                    break;
                case 3:
                    type3.add(m);
                    break;
                case 4:
                    type4.add(m);
                    break;
                case 5:
                    type5.add(m);
                    break;
                default:
            }
        }

        model.addAttribute("menu", menu);
        model.addAttribute("type1", type1);
        model.addAttribute("type2", type2);
        model.addAttribute("type3", type3);
        model.addAttribute("type4", type4);
        model.addAttribute("type5", type5);
        return "menu_management";
    }

    @GetMapping("/add_menu") //메뉴 추가 화면
    public String add_menu1(Model model){
            return "add_menu";
    }

    @PostMapping("/add_menu") //메뉴 추가 화면
    public String add_menu2(MenuSaveRequestDto menuSaveRequestDto, MultipartFile file) throws IOException {
        String imgPath = s3Service.upload(file);
        menuSaveRequestDto.setMenuImg(imgPath);

        menuService.save(menuSaveRequestDto);

        return "redirect:menu_management";
    }

    @GetMapping("/edit_menu") //메뉴 수정 화면
    public String edit_menu1(Model model){
        return "edit_menu";
    }

    @GetMapping("/edit_menu/{menuId}") //메뉴 수정 화면
    public String edit_menu1(@PathVariable Long menuId, Model model){
        MenuResponseDto menu = menuService.findById(menuId);
        model.addAttribute("menu", menu);
        return "edit_menu";
    }

    @PostMapping("/edit_menu/{menuId}") //메뉴 수정 화면
    public String edit_menu2(@PathVariable Long menuId, MenuUpdateRequestDto menuUpdateRequestDto, MultipartFile file) throws IOException {
        if(file.isEmpty()){
            MenuResponseDto menu = menuService.findById(menuId);
            menuUpdateRequestDto.setMenuImg(menu.getMenuImg());
        }

        else{
            String imgPath = s3Service.upload(file);
            menuUpdateRequestDto.setMenuImg(imgPath);
        }

        menuService.update(menuId, menuUpdateRequestDto);

        return "redirect:/menu_management";
    }
}


