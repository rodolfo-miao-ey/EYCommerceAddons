package br.com.ey.storefront.controllers.misc;

import br.com.ey.core.model.CustomerWishListModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.*;


@Controller
@RequestMapping("/wishlist")
public class WishListRestController {


    @Resource(name = "timeService")
    private TimeService timeService;

    @Resource
    private ProductService productService;

    @Resource
    private UserService userService;

    @Resource
    private ModelService modelService;

    @Resource(name = "productVariantFacade")
    private ProductFacade productFacade;

    @Resource
    private SessionService sessionService;


    @GetMapping("/add/{productID}") // Adicionar o produto na tabela de desejos do cliente
    public ResponseEntity<String> addWishList(@PathVariable String productID) {

        Boolean found = false;

        final List<ProductOption> options = new ArrayList<>(Arrays.asList(ProductOption.VARIANT_FIRST_VARIANT, ProductOption.BASIC,
                ProductOption.URL));


        final CustomerModel customerModel = (CustomerModel) userService.getCurrentUser();

        if (customerModel.getUid().equals("anonymous")){
            final ProductData productData = productFacade.getProductForCodeAndOptions(productID, options);
            sessionService.setAttribute("productRedirect", productData.getUrl());
            return ResponseEntity.ok("Login");
        }

        List<CustomerWishListModel> wishlist = new ArrayList<>();
        if (customerModel.getCustomerWishList() != null && !customerModel.getCustomerWishList().isEmpty()) {
            wishlist.addAll(customerModel.getCustomerWishList());
        }

        for (final Iterator<CustomerWishListModel> itr = wishlist.iterator(); itr.hasNext(); ) {
            final CustomerWishListModel wish = itr.next();

            if (wish.getProduct().equals(productID)) {
                found = true;
            }
        }

        if (!found) {
            CustomerWishListModel wishData = new CustomerWishListModel();
            wishData.setDateTime(timeService.getCurrentTime().getTime());
            wishData.setProduct(productID);
            modelService.save(wishData);
            wishlist.add(wishData);
        }

        if (!wishlist.isEmpty()) {
            customerModel.setCustomerWishList(wishlist);
            modelService.save(customerModel);
        }

        return ResponseEntity.ok("ok");

    }


    @GetMapping("/delete/{productID}") // Deletar o produto na tabela de desejos do cliente
    public ResponseEntity<String> deleteWishList(@PathVariable String productID) {

        Boolean found = false;
        final CustomerModel customerModel = (CustomerModel) userService.getCurrentUser();
        if (customerModel.getUid().equals("anonymous")){
            return ResponseEntity.ok("ok");
        }

        List<CustomerWishListModel> wishList = new ArrayList<>(customerModel.getCustomerWishList());

        for (final Iterator<CustomerWishListModel> itr = wishList.iterator(); itr.hasNext(); ) {
            final CustomerWishListModel wish = itr.next();
            if(wish.getProduct().equals(productID)){
                itr.remove();
                modelService.remove(wish);
                break;
            }
        }
        customerModel.setCustomerWishList(wishList);
        modelService.save(customerModel);

        return ResponseEntity.ok("ok");

    }


}