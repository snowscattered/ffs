class API {
    static baseURL = "/";
    static basePath = "/";

    static Upload(file) {
        var obj;
        if(file.files.length<=0) {
            return obj = {"fileName": "error", "code": -1, "message": "error"};
        }
        var formData=new FormData();
        formData.append("file",file.files[0])
        $.ajax({
            type: "POST",
            url:this.baseURL + "upload",
            async: false,
            data: formData,
            timeout: 2000,
            dataType: "json",
            cache: false,
            processData: false,
            contentType: false,
            success: function (data) {
                obj=data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else {
                    obj = {"fileName": "error", "code": -1, "message": "error"}
                }
            }
        })
        return obj;
    }



    static getUser(uid, role, name, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/user/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "block": null,
                    "uid": uid === ""?null:uid,
                    "role": role === ""?null:role,
                    "name": name === ""?null:name,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                },
            });
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/get",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "block": null,
                "uid": uid === ""?null:uid,
                "role": role === ""?null:role,
                "name": name === ""?null:name,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static addUser(user) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "username": user.username === ""?null:user.username,
                "password": user.password === ""?null:user.password,
                "name": user.name === ""?null:user.name,
                "role": user.role === ""?null:user.role,
                "image": user.image,
                "tel": user.tel,
                "address": user.address,
                "info": user.info,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static updateUser(user, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/user/upd",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "uid": user.uid === ""?null:user.uid,
                    "username": user.username === ""?null:user.username,
                    "password": user.password === ""?null:user.password,
                    "name": user.name === ""?null:user.name,
                    "image": user.image === ""?null:user.image,
                    "tel": user.tel === ""?null:user.tel,
                    "address": user.address === ""?null:user.address,
                    "info": user.info === ""?null:user.info,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            });
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/upd",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "uid": user.uid === ""?null:user.uid,
                "username": user.username === ""?null:user.username,
                "password": user.password === ""?null:user.password,
                "name": user.name === ""?null:user.name,
                "image": user.image === ""?null:user.image,
                "tel": user.tel === ""?null:user.tel,
                "address": user.address === ""?null:user.address,
                "info": user.info === ""?null:user.info,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static deleteUser(uid, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/user/del",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "uid": uid,
                }),
                timeout: 2000,
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            })
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/del",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "uid": uid
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }



    static getProduct(pid, uid, name, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/product/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "block": null,
                    "pid": pid === ""?null:pid,
                    "uid": uid === ""?null:uid,
                    "name": name === ""?null:name,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                },
            })
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/product/get",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "block": null,
                "pid": pid === ""?null:pid,
                "uid": uid === ""?null:uid,
                "name": name === ""?null:name,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            },
        })
        return obj;
    }
    static addProduct(product) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/product/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "uid": product.uid == ""?null:product.uid,
                "name": product.name == ""?null:product.name,
                "price": product.price == ""?null:product.price,
                "score": product.score == ""?null:product.score,
                "image": "default.png",
                "info": product.info == ""?null:product.info,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            },
        })
        return obj;
    }
    static updateProduct(product, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/product/upd",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "pid": product.pid,
                    "uid": product.uid,
                    "name": product.name,
                    "image": product.image,
                    "price": product.price,
                    "info": product.info,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            });
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/product/upd",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "pid": product.pid,
                "uid": product.uid,
                "name": product.name,
                "image": product.image,
                "price": product.price,
                "info": product.info,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static deleteProduct(pid, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/product/del",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "pid": pid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            });
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/product/del",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "pid": pid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }


    static getOrder(oid, tid, uid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/order/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "block": null,
                    "oid": oid == ""?null:oid,
                    "tid": tid == ""?null:tid,
                    "uid": uid == ""?null:uid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/order/get",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "block": null,
                "oid": oid == ""?null:oid,
                "tid": tid == ""?null:tid,
                "uid": uid == ""?null:uid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static addOrder(order) {
        var obj;
        order.token = $.cookie("token");
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/order/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify(order),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            },
        })
        return obj;
    }
    static updateOrder(order, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/order/upd",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "state": order.state == ""?null:order.state,
                    "oid": order.oid == ""?null:order.oid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
            return ;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/order/upd",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "state": order.state == ""?null:order.state,
                "oid": order.oid == ""?null:order.oid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static deleteOrder(oid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/order/del",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "oid": oid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
            return ;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/order/del",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "oid": oid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }


    static getReview(rid, oid, uid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/review/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "block": null,
                    "rid": rid == ""?null:rid,
                    "oid": oid == ""?null:oid,
                    "uid": uid == ""?null:uid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    if(data.reviews === null || data.reviews === undefined)
                        data.reviews=[];
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/review/get",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "block": null,
                "rid": rid == ""?null:rid,
                "oid": oid == ""?null:oid,
                "uid": uid == ""?null:uid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static addReview(oid ,review) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/review/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "oid": oid,
                "detail": review.detail,
                "score": review.score,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            },
        })
        return obj;
    }
    static updateReview(review,callback,args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/review/upd",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "rid": review.rid,
                    "detail": review.detail,
                    "score": review.score,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/review/upd",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "rid": review.rid,
                "detail": review.detail,
                "score": review.score,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static deleteReview(rid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/review/del",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "rid": rid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/review/del",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "rid": rid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }

}