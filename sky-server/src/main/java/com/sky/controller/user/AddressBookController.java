package com.sky.controller.user;


import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/user/addressBook")
@Api(tags = "Address relevant apis")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * get all addresses under the current user
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("get all addresses under the current user")
    public Result<List<AddressBook>> list(){
        AddressBook  addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /**
     * add new address
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("add new address")
    public Result save(@RequestBody AddressBook addressBook){
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * get address by id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("get address by id")
    public Result<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * update address
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("update address by id")
    public Result update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * set default address
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("set default address")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * delete address by id
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("delete address by id")
    public Result deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

    /**
     * get default address
     */
    @GetMapping("default")
    @ApiOperation("get default address")
    public Result<AddressBook> getDefault() {
        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);

        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }

        return Result.error("Default address is not found");
    }

}
