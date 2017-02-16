package org.j.products.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.j.products.search.products.Result;
import org.j.products.search.products.SearchService;
import org.j.products.search.products.SortOrder;
import org.j.products.web.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Andrew on 2/9/17.
 */
@Controller
@RequestMapping("/")
public class IndexController {
    private static final Logger logger = LogManager.getLogger(IndexController.class);

    private static final Integer PAGE_SIZE = 15;

    @Autowired
    private SearchService searchService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(
            @RequestParam(value = "p", required = false) Integer page,
            @RequestParam(value = "s", required = false) String orderString,
            @RequestParam(value = "search", required = false) final String search,
            final ModelMap model) {

        logger.debug(orderString);

        final SortOrder order = parseSortOrder(orderString);

        logger.debug(order);

        if (null == order) {
            throw new NotFoundException();
        }

        orderString = order.toString();

        if (null == page) {
            page = 1;
        } else if (page < 1) {
            throw new NotFoundException();
        }

        final Result result = searchService.getQueryOptions()
                .setQuery(search)
                .search(new PageRequest(page - 1, PAGE_SIZE, new Sort(order)));

        model.addAttribute("urlPath", "/");
        model.addAttribute("search", search);
        model.addAttribute("page", page);
        model.addAttribute("orderString", orderString);
        model.addAttribute("orders", searchService.getAwailableSortOrders());

        model.addAttribute("products", result.getProducts());

        return "index";
    }

    private SortOrder parseSortOrder(final String s) {
        SortOrder[] orders = searchService.getAwailableSortOrders();

        assert orders.length > 0;

        for(SortOrder order: orders) {
            if (order.toString().equals(s)) {
                return order;
            }
        }

        return null == s || s.isEmpty() ? orders[0] : null;
    }

    @RequestMapping(path = "welcome", method = RequestMethod.GET)
    public String sayHello(ModelMap model) {
        model.addAttribute("greeting", "Hello, world");
        return "welcome";
    }
}