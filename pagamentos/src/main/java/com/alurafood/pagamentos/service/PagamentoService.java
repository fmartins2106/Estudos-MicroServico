package com.alurafood.pagamentos.service;

import com.alurafood.pagamentos.dto.PagamentoDto;
import com.alurafood.pagamentos.http.PedidoClient;
import com.alurafood.pagamentos.model.Pagamento;
import com.alurafood.pagamentos.model.Status;
import com.alurafood.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ModelMapper modelMapper;
    private final PedidoClient pedido;

    public PagamentoService(PagamentoRepository pagamentoRepository, ModelMapper modelMapper, PedidoClient pedido) {
        this.pagamentoRepository = pagamentoRepository;
        this.modelMapper = modelMapper;
        this.pedido = pedido;
    }


    public Page<PagamentoDto> obterTodos(Pageable paginacao){
        return pagamentoRepository.findAll(paginacao)
                .map(p -> modelMapper.map(p, PagamentoDto.class));
    }

    public PagamentoDto obterPorId(Long id){
        Pagamento pagamento =
                pagamentoRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("ID não encontrado."));

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public PagamentoDto criarPagamento(PagamentoDto dto){
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento);
        return modelMapper.map(pagamento, PagamentoDto.class);
    }


    public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto){
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setId(id);
        pagamento = pagamentoRepository.save(pagamento);
        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public void excluirPagamento(Long id){
        pagamentoRepository.deleteById(id);
    }

    public void confirmarPagamento(Long id){
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        pagamentoRepository.save(pagamento.get());
        pedido.atualizarPagamento(pagamento.get().getPedidoId());
    }















}
