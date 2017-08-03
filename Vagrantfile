# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

unless Vagrant.has_plugin?("vagrant-cachier")
  puts "Install vagrant-cachier"
  exec 'vagrant plugin install vagrant-cachier && vagrant up'
end

unless Vagrant.has_plugin?("vagrant-vbguest")
  puts "Install vagrant-vbguest"
  exec 'vagrant plugin install vagrant-vbguest && vagrant up'
end

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.define "dev", primary: true do |dev|
    dev.vm.box = "ubuntu/trusty64"

    dev.vm.hostname = "vagrant-see-queue-are-ess"
    dev.vm.network :private_network, type: "dhcp"

    dev.vm.provision "ansible" do |ansible|
      ansible.playbook = "ansible/playbook.yml"
    end

    dev.cache.scope = :machine

    dev.vm.provider :virtualbox do |vb|
      vb.memory = 1024
      vb.cpus = 2
    end
  end

  config.vm.define "ci" do |ci|
    ci.vm.box = "ubuntu/trusty64"

    ci.vm.hostname = "vagrant-see-queue-are-ess-ci"
    ci.vm.network :private_network, type: "dhcp"

    ci.vm.provision "ansible" do |ansible|
      ansible.playbook = "ansible/playbook-ci.yml"
    end

    ci.cache.scope = :machine

    ci.vm.provider :virtualbox do |vb|
      vb.memory = 1024
      vb.cpus = 2
    end
  end
end
