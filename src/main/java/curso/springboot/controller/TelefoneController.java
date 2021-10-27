package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class TelefoneController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {

		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView modelView = new ModelAndView("cadastro/telefones");
		modelView.addObject("pessoaobj", pessoa.get());
		modelView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));

		return modelView;
	}

	@PostMapping("**/addfonepessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {

		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();

		if (telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {
			ModelAndView modelView = new ModelAndView("cadastro/telefones");
			modelView.addObject("pessoaobj", pessoa);
			modelView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			List<String> msg = new ArrayList<String>();
			if (telefone.getNumero().isEmpty()) {
				msg.add("Numero deve ser informado");
			}
			if (telefone.getTipo().isEmpty()) {
				msg.add("Tipo deve ser informado");
			}
			modelView.addObject("msg", msg);
			return modelView;

		}

		ModelAndView modelView = new ModelAndView("cadastro/telefones");

		telefone.setPessoa(pessoa);

		telefoneRepository.save(telefone);

		modelView.addObject("pessoaobj", pessoa);
		modelView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));

		return modelView;
	}

	@GetMapping("/excluirtelefone/{idtelefone}")
	public ModelAndView excluirTelefone(@PathVariable("idtelefone") Long idtelefone) {

		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();

		telefoneRepository.deleteById(idtelefone);

		ModelAndView modelView = new ModelAndView("cadastro/telefones");
		modelView.addObject("pessoaobj", pessoa);
		modelView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));

		return modelView;
	}
}
