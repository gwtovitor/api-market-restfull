package com.market.market.controlers;

import com.market.market.dtos.ProdutoRecordDTO;
import com.market.market.models.ProdutoModel;
import com.market.market.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProdutoControler {

    @Autowired
    ProdutoRepository produtoRepository;

    @PostMapping("/produtos")
    public ResponseEntity<ProdutoModel> saveProduct(@RequestBody @Valid ProdutoRecordDTO produtoRecordDTO){

        var produtoModel = new ProdutoModel();
        BeanUtils.copyProperties(produtoRecordDTO, produtoModel);
        System.out.println(produtoRecordDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoRepository.save(produtoModel));
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<ProdutoModel>> getAllProdutos(){
        List<ProdutoModel> produtoList = produtoRepository.findAll();
        if(!produtoList.isEmpty()){
            for(ProdutoModel produto: produtoList){
                UUID id = produto.getIdProduto();
                produto.add(linkTo(methodOn(ProdutoControler.class).getOneProduto(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(produtoList);
    }

    @GetMapping("/produtos/{id}")
    public ResponseEntity<Object>getOneProduto(@PathVariable(value="id") UUID id){
        Optional<ProdutoModel> produto0 = produtoRepository.findById(id);
        if(produto0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        produto0.get().add(linkTo(methodOn(ProdutoControler.class).getAllProdutos()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(produto0.get());
    }
    @PutMapping("/produtos/{id}")
    public ResponseEntity<Object>updateProduto(@PathVariable(value="id") UUID id,
                                               @RequestBody @Valid ProdutoRecordDTO produtoRecordDTO){
        Optional<ProdutoModel> produto0 = produtoRepository.findById(id);
        System.out.println("EstouAqui");
        if(produto0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        var productModel = produto0.get();
        BeanUtils.copyProperties(produtoRecordDTO, productModel);
        return ResponseEntity.status(HttpStatus.OK).body(produtoRepository.save(productModel));
    }
    @DeleteMapping("/produtos/{id}")
    public ResponseEntity<Object>deletarProduto(@PathVariable(value="id") UUID id){
        Optional<ProdutoModel> produto0 = produtoRepository.findById(id);
        if(produto0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");

        }
        produtoRepository.delete(produto0.get());
        return ResponseEntity.status(HttpStatus.OK).body("Produto Deletado");
    }
}
