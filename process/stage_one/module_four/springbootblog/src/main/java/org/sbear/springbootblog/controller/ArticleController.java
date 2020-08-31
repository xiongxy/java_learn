package org.sbear.springbootblog.controller;
import org.sbear.springbootblog.pojo.Article;
import org.sbear.springbootblog.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xxyWi
 */
@Controller
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @RequestMapping("/list")
    public String findListByPage(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "2") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Article> pageList = articleRepository.findAll(pageable);

        System.out.println("总页数" + pageList.getTotalPages());
        System.out.println("当前页是：" + page);

        model.addAttribute("articlePageList", pageList);
        return "client/index";
    }
}
